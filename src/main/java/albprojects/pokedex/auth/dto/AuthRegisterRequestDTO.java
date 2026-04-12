package albprojects.pokedex.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO for user registration input payload.
@Schema( description = "Registration request payload" )
public record AuthRegisterRequestDTO(
    @NotBlank
    @Size( min = 3, max = 50 )
    @Schema( example = "ash-ketchum" )
    String username,

    @NotBlank
    @Email
    @Size( max = 100 )
    @Schema( example = "ash@pokedex.com" )
    String email,

    @NotBlank
    @Size( min = 8, max = 100 )
    @Schema( example = "Pikachu@123" )
    String password
)
{
}
