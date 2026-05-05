package albprojects.pokedex.common.config;

import albprojects.pokedex.auth.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig( JwtAuthenticationFilter jwtAuthenticationFilter, CustomUserDetailsService customUserDetailsService )
    {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    public static final String URL_API_POKEMONS = "/api/pokemons/**";
    public static final String URL_API_POKEMONS_ROOT = "/api/pokemons";
    public static final String URL_API_POKEMONS_ITEM = "/api/pokemons/*";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";
    public static final String URL_H2_CONSOLE = "/h2-console/**";
    public static final String URL_API_POKEMONS_EXTERNAL = "/api/pokemons/external/**";

    // Configures the authentication provider to use our custom user details service and password encoder.
    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider( customUserDetailsService );
        authProvider.setPasswordEncoder( passwordEncoder() );
        return authProvider;
    }

    // Defines the password hashing strategy used for storing user passwords securely.
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    // Exposes the AuthenticationManager as a Bean to be used in the authentication service.
    @Bean
    public AuthenticationManager authenticationManager( AuthenticationConfiguration authenticationConfiguration ) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configures the main security filter chain for the application.
    @Bean
    public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception
    {
        http
            // Disable CSRF protection as this is a stateless REST API.
            .csrf( AbstractHttpConfigurer::disable )
            // Allow the H2 console to be rendered in a frame.
            .headers( headers -> headers.frameOptions( frame -> frame.sameOrigin() ) )
            // Disable default form-based and basic authentication.
            .formLogin( AbstractHttpConfigurer::disable )
            .httpBasic( AbstractHttpConfigurer::disable )
            // Enforce stateless session management; no sessions will be created.
            .sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )
            // Define authorization rules for HTTP requests.
            .authorizeHttpRequests( auth -> auth
                // Publicly accessible authentication endpoints.
                .requestMatchers( "/api/auth/**" ).permitAll()
                // Publicly accessible API documentation.
                .requestMatchers( "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**" ).permitAll()
                // Publicly accessible H2 console for local development.
                .requestMatchers( URL_H2_CONSOLE ).permitAll()
                // External Pokemon data management requires ADMIN role.
                .requestMatchers( HttpMethod.GET, URL_API_POKEMONS_EXTERNAL ).hasRole( ADMIN_ROLE )
                // Importing external Pokemon data requires ADMIN role.
                .requestMatchers( HttpMethod.POST, URL_API_POKEMONS_EXTERNAL ).hasRole( ADMIN_ROLE )
                // Read operations require USER or ADMIN role.
                .requestMatchers( HttpMethod.GET, URL_API_POKEMONS ).hasAnyRole( USER_ROLE, ADMIN_ROLE )
                // Capture operation requires USER or ADMIN role.
                .requestMatchers( HttpMethod.POST, URL_API_POKEMONS_ITEM ).hasAnyRole( USER_ROLE, ADMIN_ROLE )
                // Write operations (create, update, delete) require ADMIN role.
                .requestMatchers( HttpMethod.POST, URL_API_POKEMONS_ROOT ).hasRole( ADMIN_ROLE )
                .requestMatchers( HttpMethod.PUT, URL_API_POKEMONS ).hasRole( ADMIN_ROLE )
                .requestMatchers( HttpMethod.DELETE, URL_API_POKEMONS ).hasRole( ADMIN_ROLE )
                // Any other request must be authenticated.
                .anyRequest().authenticated() )
            // Register the custom authentication provider.
            .authenticationProvider( authenticationProvider() )
            // Add the JWT filter before the standard username/password authentication filter.
            .addFilterBefore( jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class )
            // Return 401 Unauthorized for unauthenticated requests instead of the default 403.
            .exceptionHandling( ex -> ex
                .authenticationEntryPoint( (request, response, authException) ->
                    response.sendError( jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" ) ) );

        return http.build();
    }
}
