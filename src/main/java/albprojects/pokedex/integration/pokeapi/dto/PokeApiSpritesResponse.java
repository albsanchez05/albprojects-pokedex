package albprojects.pokedex.integration.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public record PokeApiSpritesResponse(
    @JsonProperty( "front_default" ) String frontDefault
) {}