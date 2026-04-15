package albprojects.pokedex.common.config;

import albprojects.pokedex.auth.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Filter that intercepts every request, extracts and validates the JWT, and sets the authentication in the security context.
// OncePerRequestFilter guarantees this filter runs exactly once per request.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter( JwtService jwtService, CustomUserDetailsService userDetailsService )
    {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException
    {


        // Read the Authorization header from the incoming request.
        String authHeader = request.getHeader( "Authorization" );

        // If the header is missing or does not start with "Bearer ", skip this filter entirely.
        if ( authHeader == null || !authHeader.startsWith( "Bearer " ) )
        {
            filterChain.doFilter( request, response );
            return;
        }

        // Strip the "Bearer " prefix to get the raw JWT string.
        // The substring(7) call removes the first 7 characters ("Bearer ") from the header value, leaving just the JWT token.
        String jwt = authHeader.substring( 7 );

        try
        {
            // Extract the username from the token payload.
            String username = jwtService.extractUsername( jwt );

            // Only proceed if username was extracted and no authentication is set for this request yet.
            if ( username != null && SecurityContextHolder.getContext().getAuthentication() == null )
            {
                // Load the full user record from the database.
                UserDetails userDetails = userDetailsService.loadUserByUsername( username );

                // Validate the token against the loaded user ( signature, expiration, ownership ).
                if ( jwtService.isTokenValid( jwt, userDetails ) )
                {
                    // Create a Spring Security authentication token with the user's authorities.
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    // Attach request-level details ( e.g., IP address, session ) to the auth token.
                    authToken.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );
                    // Register the authenticated user in the security context for this request.
                    SecurityContextHolder.getContext().setAuthentication( authToken );
                }
            }
        }
        // If any exception occurs during token extraction or validation, clear the security context and return a 401 Unauthorized response.
        catch ( JwtException | IllegalArgumentException e )
        {
            SecurityContextHolder.clearContext();
            response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token" );
            return;
        }

        // Pass the request along to the next filter in the chain.
        filterChain.doFilter( request, response );
    }
}
