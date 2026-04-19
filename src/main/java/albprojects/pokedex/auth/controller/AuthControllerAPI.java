package albprojects.pokedex.auth.controller;

import albprojects.pokedex.auth.dto.AuthLoginRequestDTO;
import albprojects.pokedex.auth.dto.AuthRegisterRequestDTO;
import albprojects.pokedex.auth.dto.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag( name = "Auth API", description = "API for authentication and token generation" )
public interface AuthControllerAPI
{
    @Operation( summary = "Register a new user", description = "Creates a new user account and returns a JWT token" )
    @ApiResponses( {
        @ApiResponse(
            responseCode = "201", // Correct code for resource creation
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject( value = "{ \"token\": \"eyJhbGciOiJIUzI1NiJ9...\" }" )
            )
        ),
        @ApiResponse(
            responseCode = "409", // Correct code for conflict
            description = "Username or email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject( value = "{ \"error\": \"Username or email already exists\" }" )
            )
        )
    } )
    ResponseEntity<?> register( @Valid @RequestBody AuthRegisterRequestDTO request );

    @Operation( summary = "Login user", description = "Authenticates an existing user and returns a JWT token" )
    @ApiResponses( {
        @ApiResponse(
            responseCode = "200",
            description = "User authenticated successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject( value = "{ \"token\": \"eyJhbGciOiJIUzI1NiJ9...\" }" )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject( value = "{ \"error\": \"Invalid username or password\" }" )
            )
        )
    } )
    AuthResponseDTO login( @Valid @RequestBody AuthLoginRequestDTO request );
}
