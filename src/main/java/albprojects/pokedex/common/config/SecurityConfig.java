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

@Configuration // tells Spring that this class contains configuration settings
@EnableWebSecurity // enables Spring Security's web security support and provides Spring MVC integration
public class SecurityConfig
{
    public static final String URL_API_POKEMONS = "/api/pokemons/**";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";

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
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
