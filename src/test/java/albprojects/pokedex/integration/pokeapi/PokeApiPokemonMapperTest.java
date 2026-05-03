package albprojects.pokedex.integration.pokeapi;

import albprojects.pokedex.integration.pokeapi.dto.PokeApiPokemonResponse;
import albprojects.pokedex.integration.pokeapi.dto.PokeApiSpritesResponse;
import albprojects.pokedex.integration.pokeapi.dto.PokeApiStatResponse;
import albprojects.pokedex.integration.pokeapi.dto.PokeApiStatSlotResponse;
import albprojects.pokedex.integration.pokeapi.dto.PokeApiTypeResponse;
import albprojects.pokedex.integration.pokeapi.dto.PokeApiTypeSlotResponse;
import albprojects.pokedex.pokemon.dto.PokemonCompleteDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PokeApiPokemonMapperTest
{
    private final PokeApiPokemonMapper mapper = new PokeApiPokemonMapper();

    @Test
    void shouldMapExternalResponseToPokemonCompleteDTO()
    {
        PokeApiPokemonResponse source = new PokeApiPokemonResponse(
            1,
            "bulbasaur",
            List.of(
                new PokeApiTypeSlotResponse( 2, new PokeApiTypeResponse( "poison" ) ),
                new PokeApiTypeSlotResponse( 1, new PokeApiTypeResponse( "grass" ) )
            ),
            List.of(
                new PokeApiStatSlotResponse( 45, new PokeApiStatResponse( "hp" ) ),
                new PokeApiStatSlotResponse( 49, new PokeApiStatResponse( "attack" ) ),
                new PokeApiStatSlotResponse( 49, new PokeApiStatResponse( "defense" ) ),
                new PokeApiStatSlotResponse( 65, new PokeApiStatResponse( "special-attack" ) ),
                new PokeApiStatSlotResponse( 65, new PokeApiStatResponse( "special-defense" ) ),
                new PokeApiStatSlotResponse( 45, new PokeApiStatResponse( "speed" ) )
            ),
            new PokeApiSpritesResponse( "https://img/pokemon/1.png" )
        );

        PokemonCompleteDTO result = mapper.toPokemonCompleteDTO( source );

        assertEquals( 1, result.pokemonId() );
        assertEquals( "Bulbasaur", result.name() );
        assertEquals( "Grass", result.type1() );
        assertEquals( "Poison", result.type2() );
        assertEquals( 45, result.hp() );
        assertEquals( 49, result.attack() );
        assertEquals( 49, result.defense() );
        assertEquals( 65, result.spAttack() );
        assertEquals( 65, result.spDefense() );
        assertEquals( 45, result.speed() );
        assertEquals( "https://img/pokemon/1.png", result.image() );
        assertFalse( result.captured() );
    }
}
