package albprojects.pokedex.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import albprojects.pokedex.auth.service.CustomUserDetailsService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // tells Spring that this class contains configuration settings
@EnableWebSecurity // enables Spring Security's web security support and provides Spring MVC integration
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
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";

    // Configures the authentication provider to use our custom user details service and password encoder.
    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService( customUserDetailsService );
        authProvider.setPasswordEncoder( passwordEncoder() );
        return authProvider;
    }

    // Password hashing strategy used for storing user passwords securely.
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    // Exposes the AuthenticationManager so services/controllers can use it for authenticating users.
    @Bean
    public AuthenticationManager authenticationManager ( AuthenticationConfiguration authenticationConfiguration ) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //
    @Bean
    public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception
    {
        http
            // Disable CSRF because this is a stateless REST API.
            .csrf( AbstractHttpConfigurer::disable )
            // Disable browser-based login mechanisms.
            .formLogin( AbstractHttpConfigurer::disable )
            .httpBasic( AbstractHttpConfigurer::disable )
            // Enforce stateless session management.
            .sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )
            .authorizeHttpRequests( auth -> auth
                // Public authentication endpoints.
                .requestMatchers( "/api/auth/**" ).permitAll()
                // Keep Swagger/OpenAPI docs publicly accessible for now.
                .requestMatchers( "/swagger-ui/**", "/v3/api-docs/**" ).permitAll()
                // Read operations: USER or ADMIN.
                .requestMatchers( HttpMethod.GET, URL_API_POKEMONS ).hasAnyRole( USER_ROLE, ADMIN_ROLE )
                // Write operations: ADMIN only.
                .requestMatchers( HttpMethod.POST, URL_API_POKEMONS ).hasRole( ADMIN_ROLE )
                .requestMatchers( HttpMethod.PUT, URL_API_POKEMONS ).hasRole( ADMIN_ROLE )
                .requestMatchers( HttpMethod.DELETE, URL_API_POKEMONS ).hasRole( ADMIN_ROLE )
                // Any other endpoint must be authenticated.
                .anyRequest().authenticated() )
            // Register the custom authentication provider and JWT filter in the security filter chain.
            .authenticationProvider( authenticationProvider() )
            .addFilterBefore( jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class );

        return http.build();
    }
}
