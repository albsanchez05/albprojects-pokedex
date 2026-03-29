package albprojects.pokedex.dto;

public record PokemonCompleteDTO (
        Integer pokemonId,
        String name,
        String type1,
        String type2,
        Integer hp,
        Integer attack,
        Integer defense,
        Integer spAttack,
        Integer spDefense,
        Integer speed,
        String image
) {}
