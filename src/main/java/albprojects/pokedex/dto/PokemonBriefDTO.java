package albprojects.pokedex.dto;

// Record class for Pokemon Data Transfer Object (DTO), currently empty but can be expanded with fields as needed for transferring Pokemon data between layers of the application

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Data Transfer Object for brief Pokemon information, including ID, name, and image URL." )
public record PokemonBriefDTO( 
        @Schema( example = "1" ) Integer pokemonId, 
        @Schema( example = "Bulbasaur" ) String name, 
        @Schema( example = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png" ) String image 
)
{
}
