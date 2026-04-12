package albprojects.pokedex.auth.controller;

import albprojects.pokedex.auth.dto.AuthLoginRequestDTO;
import albprojects.pokedex.auth.dto.AuthRegisterRequestDTO;
import albprojects.pokedex.auth.dto.AuthResponseDTO;
import albprojects.pokedex.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/auth" )
public class AuthController implements AuthControllerAPI
{
    private final AuthService authService;

    public AuthController( AuthService authService )
    {
        this.authService = authService;
    }

    @Override
    @PostMapping( "/register" )
    public AuthResponseDTO register( @Valid @RequestBody AuthRegisterRequestDTO request )
    {
        String token = authService.register( request.username(), request.email(), request.password() );
        return new AuthResponseDTO( token );
    }

    @Override
    @PostMapping( "/login" )
    public AuthResponseDTO login( @Valid @RequestBody AuthLoginRequestDTO request )
    {
        String token = authService.login( request.username(), request.password() );
        return new AuthResponseDTO( token );
    }
}
