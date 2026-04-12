package albprojects.pokedex.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO for login input payload.
@Schema( description = "Login request payload" )
public record AuthLoginRequestDTO(
    @NotBlank
    @Size( min = 3, max = 50 )
    @Schema( example = "ash-ketchum" )
    String username,

    @NotBlank
    @Size( min = 8, max = 100 )
    @Schema( example = "Pikachu@123" )
    String password
)
{
}
