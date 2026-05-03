package albprojects.pokedex.integration.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties( ignoreUnknown = true )
public record PokeApiPokemonResponse(
    Integer id,
    String name,
    List<PokeApiTypeSlotResponse> types,
    List<PokeApiStatSlotResponse> stats,
    PokeApiSpritesResponse sprites
) {}