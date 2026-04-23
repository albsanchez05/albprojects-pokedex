package albprojects.pokedex.auth;

import albprojects.pokedex.auth.model.User;
import albprojects.pokedex.auth.repository.UserRepository;
import albprojects.pokedex.auth.service.AuthService;
import albprojects.pokedex.auth.service.CustomUserDetailsService;
import albprojects.pokedex.common.config.JwtService;
import albprojects.pokedex.common.exceptions.InvalidCredentialsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith( MockitoExtension.class )
@DisplayName( "AuthService Unit Tests" )
class AuthServiceTest
{
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthService authService;

    // Helper: build a Spring Security UserDetails object for a regular USER.
    private UserDetails buildUserDetails( String username )
    {
        return new org.springframework.security.core.userdetails.User(
            username,
            "hashed-password",
            List.of( new SimpleGrantedAuthority( "ROLE_USER" ) )
        );
    }

    // ---- register() tests ----

    @Test
    @DisplayName( "register() should save a new user and return a JWT token" )
    void testRegisterSuccess( )
    {
        // Arrange: no conflicting user exists yet.
        when( userRepository.existsByUsername( "ash" ) ).thenReturn( false );
        when( userRepository.existsByEmail( "ash@pokedex.com" ) ).thenReturn( false );
        when( passwordEncoder.encode( "Pikachu@123" ) ).thenReturn( "hashed-password" );
        when( userRepository.save( any( User.class ) ) ).thenAnswer( invocation -> invocation.getArgument( 0 ) );

        UserDetails userDetails = buildUserDetails( "ash" );
        when( customUserDetailsService.loadUserByUsername( "ash" ) ).thenReturn( userDetails );
        when( jwtService.generateToken( userDetails ) ).thenReturn( "jwt-token" );

        // Act
        String token = authService.register( "ash", "ash@pokedex.com", "Pikachu@123" );

        // Assert
        assertEquals( "jwt-token", token );
        verify( userRepository ).save( any( User.class ) );
    }

    @Test
    @DisplayName( "register() should throw IllegalArgumentException when username is already taken" )
    void testRegisterDuplicateUsername( )
    {
        // Arrange: a user with this username already exists.
        when( userRepository.existsByUsername( "ash" ) ).thenReturn( true );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register( "ash", "new@pokedex.com", "Pikachu@123" )
        );

        assertTrue( exception.getMessage().contains( "Username already taken" ) );
        verify( userRepository, never() ).save( any() );
    }

    @Test
    @DisplayName( "register() should throw IllegalArgumentException when email is already registered" )
    void testRegisterDuplicateEmail( )
    {
        // Arrange: username is free but email is already taken.
        when( userRepository.existsByUsername( "new-ash" ) ).thenReturn( false );
        when( userRepository.existsByEmail( "taken@pokedex.com" ) ).thenReturn( true );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register( "new-ash", "taken@pokedex.com", "Pikachu@123" )
        );

        assertTrue( exception.getMessage().contains( "Email already registered" ) );
        verify( userRepository, never() ).save( any() );
    }

    // ---- login() tests ----

    @Test
    @DisplayName( "login() should return a JWT token when credentials are valid" )
    void testLoginSuccess( )
    {
        // Arrange: authentication manager accepts the credentials and returns a token.
        // authenticate() is NOT void — it returns an Authentication object.
        when( authenticationManager.authenticate( any( UsernamePasswordAuthenticationToken.class ) ) )
            .thenReturn( new UsernamePasswordAuthenticationToken( "ash", null ) );

        UserDetails userDetails = buildUserDetails( "ash" );
        when( customUserDetailsService.loadUserByUsername( "ash" ) ).thenReturn( userDetails );
        when( jwtService.generateToken( userDetails ) ).thenReturn( "jwt-token" );

        // Act
        String token = authService.login( "ash", "Pikachu@123" );

        // Assert
        assertEquals( "jwt-token", token );
        verify( authenticationManager ).authenticate( any( UsernamePasswordAuthenticationToken.class ) );
    }

    @Test
    @DisplayName( "login() should throw InvalidCredentialsException when password is wrong" )
    void testLoginInvalidCredentials( )
    {
        // Arrange: authentication manager rejects the credentials.
        doThrow( new BadCredentialsException( "Bad credentials" ) )
            .when( authenticationManager ).authenticate( any( UsernamePasswordAuthenticationToken.class ) );

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(
            InvalidCredentialsException.class,
            () -> authService.login( "ash", "wrong-password" )
        );

        assertTrue( exception.getMessage().contains( "Invalid username or password" ) );
        // The JWT service must not be called when credentials fail.
        verify( jwtService, never() ).generateToken( any() );
    }
}

