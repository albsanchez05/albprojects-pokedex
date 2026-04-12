package albprojects.pokedex.pokemon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Data Transfer Object for capturing a Pokemon, containing the Pokedex ID and capture status." )
public record PokemonCaptureDTO( 
        @Schema( example = "1" ) Integer pokedexId, 
        @Schema( example = "true" ) Boolean captured 
)
{
}
