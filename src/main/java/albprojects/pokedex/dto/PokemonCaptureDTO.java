package albprojects.pokedex.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Data Transfer Object for capturing a Pokemon, containing the Pokedex ID and capture status." )
public record PokemonCaptureDTO( Integer pokedexId, Boolean captured )
{
}
