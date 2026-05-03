package albprojects.pokedex.integration.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public record PokeApiStatSlotResponse(
    @JsonProperty( "base_stat" ) Integer baseStat,
    PokeApiStatResponse stat
) {}
