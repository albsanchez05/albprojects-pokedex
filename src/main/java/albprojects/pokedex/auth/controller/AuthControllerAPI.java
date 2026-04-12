package albprojects.pokedex.auth.controller;

import albprojects.pokedex.auth.dto.AuthLoginRequestDTO;
import albprojects.pokedex.auth.dto.AuthRegisterRequestDTO;
import albprojects.pokedex.auth.dto.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth API", description = "API for authentication and token generation")
public interface AuthControllerAPI
{
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns a JWT token")
    @ApiResponse(
        responseCode = "200",
        description = "User registered successfully",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{ \"token\": \"eyJhbGciOiJIUzI1NiJ9...\" }")
        )
    )
    @ApiResponse(responseCode = "400", description = "Invalid input data or duplicate username/email")
    AuthResponseDTO register( @Valid @RequestBody AuthRegisterRequestDTO request);

    @Operation(summary = "Login user", description = "Authenticates an existing user and returns a JWT token")
    @ApiResponse(
        responseCode = "200",
        description = "User authenticated successfully",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{ \"token\": \"eyJhbGciOiJIUzI1NiJ9...\" }")
        )
    )
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    AuthResponseDTO login(@Valid @RequestBody AuthLoginRequestDTO request);
}
