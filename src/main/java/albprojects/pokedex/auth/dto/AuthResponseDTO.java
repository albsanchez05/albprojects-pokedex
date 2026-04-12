package albprojects.pokedex.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// DTO for auth response payload.
@Schema( description = "Authentication response containing the JWT token" )
public record AuthResponseDTO(
    @Schema( example = "eyJhbGciOiJIUzI1NiJ9..." )
    String token
)
{
}
