package albprojects.pokedex.auth.service;

import albprojects.pokedex.auth.model.Role;
import albprojects.pokedex.auth.model.User;
import albprojects.pokedex.auth.repository.UserRepository;
import albprojects.pokedex.common.config.JwtService;
import albprojects.pokedex.common.exceptions.InvalidCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user registration and login flows.
 */
@Service
public class AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        CustomUserDetailsService userDetailsService
    )
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Registers a new user, validates input, and returns a JWT upon successful creation.
     * @param username The username for the new user.
     * @param email The email for the new user.
     * @param rawPassword The plain-text password for the new user.
     * @return A JWT for the newly created user.
     * @throws IllegalArgumentException if the username or email is already taken.
     */
    public String register( String username, String email, String rawPassword )
    {
        // Validate that username and email are not already taken.
        if ( userRepository.existsByUsername( username ) )
        {
            throw new IllegalArgumentException( "Username already taken: " + username );
        }
        if ( userRepository.existsByEmail( email ) )
        {
            throw new IllegalArgumentException( "Email already registered: " + email );
        }

        // Build the new user with a hashed password and default USER role.
        User user = new User();
        user.setUsername( username );
        user.setEmail( email );
        user.setPassword( passwordEncoder.encode( rawPassword ) );
        user.setRole( Role.USER );

        userRepository.save( user );

        // Load UserDetails and generate a JWT for the newly created user.
        UserDetails userDetails = userDetailsService.loadUserByUsername( username );
        return jwtService.generateToken( userDetails );
    }

    /**
     * Authenticates an existing user with a username and password.
     * @param username The user's username.
     * @param rawPassword The user's plain-text password.
     * @return A JWT token if credentials are valid.
     * @throws InvalidCredentialsException if authentication fails.
     */
    public String login( String username, String rawPassword )
    {
        try
        {
            // AuthenticationManager will throw BadCredentialsException if credentials are wrong.
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( username, rawPassword )
            );
        }
        catch ( BadCredentialsException e )
        {
            // Translate Spring's internal exception into our own domain exception for consistent error responses.
            throw new InvalidCredentialsException( "Invalid username or password" );
        }

        // If credentials passed, load user details and issue a new token.
        final UserDetails userDetails = userDetailsService.loadUserByUsername( username );
        return jwtService.generateToken( userDetails );
    }
}
