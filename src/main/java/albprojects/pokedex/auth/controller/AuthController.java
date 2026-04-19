package albprojects.pokedex.auth.controller;

import albprojects.pokedex.auth.dto.AuthLoginRequestDTO;
import albprojects.pokedex.auth.dto.AuthRegisterRequestDTO;
import albprojects.pokedex.auth.dto.AuthResponseDTO;
import albprojects.pokedex.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping( "/api/auth" )
public class    AuthController implements AuthControllerAPI
{
    private final AuthService authService;

    public AuthController( AuthService authService )
    {
        this.authService = authService;
    }

    @Override
    @PostMapping( "/register" )
    public ResponseEntity<?> register( @Valid @RequestBody AuthRegisterRequestDTO request )
    {
        try {
            String token = authService.register( request.username(), request.email(), request.password() );
            // Return 201 Created on success
            return new ResponseEntity<>( new AuthResponseDTO( token ), HttpStatus.CREATED );
        } catch ( IllegalArgumentException e ) {
            // Return 409 Conflict if the user already exists
            return new ResponseEntity<>( Map.of( "error", e.getMessage() ), HttpStatus.CONFLICT );
        }
    }

    @Override
    @PostMapping( "/login" )
    public AuthResponseDTO login( @Valid @RequestBody AuthLoginRequestDTO request )
    {
        String token = authService.login( request.username(), request.password() );
        return new AuthResponseDTO( token );
    }
}
