package albprojects.pokedex.integration.pokeapi;

import albprojects.pokedex.integration.pokeapi.dto.PokeApiPokemonResponse;
import albprojects.pokedex.integration.pokeapi.dto.PokeApiStatSlotResponse;
import albprojects.pokedex.integration.pokeapi.dto.PokeApiTypeSlotResponse;
import albprojects.pokedex.pokemon.dto.PokemonCompleteDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
public class PokeApiPokemonMapper {

    public PokemonCompleteDTO toPokemonCompleteDTO( PokeApiPokemonResponse source ) {
        List<PokeApiTypeSlotResponse> orderedTypes = source.types() == null
            ? List.of()
            : source.types().stream()
                .sorted( Comparator.comparing( PokeApiTypeSlotResponse::slot ) )
                .toList();

        String type1 = !orderedTypes.isEmpty() && orderedTypes.get( 0 ).type() != null
            ? capitalize( orderedTypes.get( 0 ).type().name() )
            : "Unknown";

        String type2 = orderedTypes.size() > 1 && orderedTypes.get( 1 ).type() != null
            ? capitalize( orderedTypes.get( 1 ).type().name() )
            : null;

        return new PokemonCompleteDTO(
            source.id(),
            capitalize( source.name() ),
            type1,
            type2,
            extractStat( source.stats(), "hp" ),
            extractStat( source.stats(), "attack" ),
            extractStat( source.stats(), "defense" ),
            extractStat( source.stats(), "special-attack" ),
            extractStat( source.stats(), "special-defense" ),
            extractStat( source.stats(), "speed" ),
            source.sprites() != null ? source.sprites().frontDefault() : "",
            false
        );
    }

    private Integer extractStat( List<PokeApiStatSlotResponse> stats, String statName ) {
        if( stats == null ) {
            return 1;
        }

        return stats.stream()
            .filter( stat -> stat.stat() != null && statName.equalsIgnoreCase( stat.stat().name() ) )
            .map( PokeApiStatSlotResponse::baseStat )
            .findFirst()
            .orElse( 1 );
    }

    private String capitalize( String value ) {
        if( value == null || value.isBlank() ) {
            return value;
        }

        String lower = value.toLowerCase( Locale.ROOT );
        return Character.toUpperCase( lower.charAt( 0 ) ) + lower.substring( 1 );
    }
}