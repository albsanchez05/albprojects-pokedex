package albprojects.pokedex.integration.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties( ignoreUnknown = true )
public record PokeApiTypeResponse(
    String name
) {}
