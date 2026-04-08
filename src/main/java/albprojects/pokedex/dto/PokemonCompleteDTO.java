package albprojects.pokedex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema( description = "Data Transfer Object for complete Pokemon information, including all relevant attributes such as ID, name, types, stats, and image URL." )
public record PokemonCompleteDTO (
        @NotNull @Positive
        @Schema( example = "1" )
        Integer pokemonId,
        @NotBlank
        @Schema( example = "Bulbasaur" )
        String name,
        @NotBlank
        @Schema( example = "Grass" )
        String type1,
        @Schema( example = "Poison" )
        String type2,
        @NotNull @Positive
        @Schema( example = "45" )
        Integer hp,
        @NotNull @Positive
        @Schema( example = "49" )
        Integer attack,
        @NotNull @Positive
        @Schema( example = "49" )
        Integer defense,
        @NotNull @Positive
        @Schema( example = "65" )
        Integer spAttack,
        @NotNull @Positive
        @Schema( example = "65" )
        Integer spDefense,
        @NotNull @Positive
        @Schema( example = "45" )
        Integer speed,
        @NotBlank
        @Schema( example = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png" )
        String image,
        @Schema( accessMode = Schema.AccessMode.READ_ONLY, example = "false", description = "Indicates if the Pokemon has been captured" )
        Boolean captured
) {}
