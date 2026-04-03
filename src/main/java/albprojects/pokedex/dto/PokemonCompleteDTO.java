package albprojects.pokedex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema( description = "Data Transfer Object for complete Pokemon information, including all relevant attributes such as ID, name, types, stats, and image URL." )
public record PokemonCompleteDTO (
        @NotNull @Positive
        Integer pokemonId,
        @NotBlank
        String name,
        @NotBlank
        String type1,
        String type2,
        @NotNull @Positive
        Integer hp,
        @NotNull @Positive
        Integer attack,
        @NotNull @Positive
        Integer defense,
        @NotNull @Positive
        Integer spAttack,
        @NotNull @Positive
        Integer spDefense,
        @NotNull @Positive
        Integer speed,
        @NotBlank
        String image,
        @Schema( hidden = true )
        Boolean captured
) {}
