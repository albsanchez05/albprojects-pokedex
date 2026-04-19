package albprojects.pokedex.common.config;

import albprojects.pokedex.auth.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
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

import java.io.IOException;

/**
 * Filter that intercepts every request to extract and validate the JWT from the Authorization header.
 * If the token is valid, it sets the user authentication in the Spring Security context.
 * This filter runs exactly once per request.
 */
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
        final String authHeader = request.getHeader( "Authorization" );

        // If the header is missing or does not start with "Bearer ", pass the request to the next filter.
        if ( authHeader == null || !authHeader.startsWith( "Bearer " ) )
        {
            filterChain.doFilter( request, response );
            return;
        }

        // Extract the JWT by removing the "Bearer " prefix.
        final String jwt = authHeader.substring( 7 );

        try
        {
            final String username = jwtService.extractUsername( jwt );

            // If username is extracted and no authentication is currently set in the context, process the token.
            if ( username != null && SecurityContextHolder.getContext().getAuthentication() == null )
            {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername( username );

                // If the token is valid, create an authentication token and set it in the security context.
                if ( jwtService.isTokenValid( jwt, userDetails ) )
                {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credentials are not needed as the user is already authenticated by the token.
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );
                    SecurityContextHolder.getContext().setAuthentication( authToken );
                }
            }
        }
        catch ( JwtException e )
        {
            // If token validation fails, we simply do nothing. The request will proceed without authentication,
            // and access will be denied later by the authorization rules if the endpoint is protected.
            // This prevents the filter from prematurely ending the request with a 401.
        }

        // Continue the filter chain.
        filterChain.doFilter( request, response );
    }
}
