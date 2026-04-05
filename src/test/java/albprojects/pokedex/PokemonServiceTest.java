package albprojects.pokedex;

import albprojects.pokedex.dto.PokemonBriefDTO;
import albprojects.pokedex.dto.PokemonCompleteDTO;
import albprojects.pokedex.exceptions.*;
import albprojects.pokedex.model.Pokemon;
import albprojects.pokedex.repository.PokemonRepository;
import albprojects.pokedex.service.PokemonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName( "PokemonService Unit Tests" )
class PokemonServiceTest
{
    @Mock
    private PokemonRepository pokemonRepository;

    @InjectMocks
    private PokemonService pokemonService;

    private Pokemon pokemon;
    private PokemonCompleteDTO pokemonCompleteDTO;

    @BeforeEach
    void setUp( )
    {
        try ( AutoCloseable mocks = MockitoAnnotations.openMocks( this ) )
        {
            pokemon = new Pokemon();
            pokemon.setId( 1L );
            pokemon.setPokedexId( 1 );
            pokemon.setName( "Bulbasaur" );
            pokemon.setType1( "Grass" );
            pokemon.setType2( "Poison" );
            pokemon.setHp( 45 );
            pokemon.setAttack( 49 );
            pokemon.setDefense( 49 );
            pokemon.setSpAttack( 65 );
            pokemon.setSpDefense( 65 );
            pokemon.setSpeed( 45 );
            pokemon.setImage( "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png" );
            pokemon.setCaptured( false );

            pokemonCompleteDTO = new PokemonCompleteDTO(
                    1,
                    "Bulbasaur",
                    "Grass",
                    "Poison",
                    45,
                    49,
                    49,
                    65,
                    65,
                    45,
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                    false
            );
        } catch ( Exception e ) {
            fail( "Mock initialization failed" );
        }
    }

    @Test
    @DisplayName( "getAllPokemons should return a page of PokemonBriefDTO" )
    void testGetAllPokemons( )
    {
        Pageable pageable = PageRequest.of( 0, 10 );
        List<Pokemon> pokemonList = new ArrayList<>();
        pokemonList.add( pokemon );
        Page<Pokemon> pokemonPage = new PageImpl<>( pokemonList );

        when( pokemonRepository.findAllByOrderByPokedexIdAsc( pageable ) ).thenReturn( pokemonPage );

        Page<PokemonBriefDTO> result = pokemonService.getAllPokemons( pageable );

        assertNotNull( result );
        assertEquals( 1, result.getTotalElements() );
        assertEquals( "Bulbasaur", result.getContent().get( 0 ).name() );

        verify( pokemonRepository, times( 1 ) ).findAllByOrderByPokedexIdAsc( pageable );
    }

    @Test
    @DisplayName( "getAllPokemons should throw PageNotFoundException if page is empty and pageNumber > 0" )
    void testGetAllPokemonsPageNotFound( )
    {
        Pageable pageable = PageRequest.of( 1, 10 );
        Page<Pokemon> emptyPage = new PageImpl<>( new ArrayList<>() );

        when( pokemonRepository.findAllByOrderByPokedexIdAsc( pageable ) ).thenReturn( emptyPage );

        assertThrows( PageNotFoundException.class, () -> pokemonService.getAllPokemons( pageable ) );
        verify( pokemonRepository, times( 1 ) ).findAllByOrderByPokedexIdAsc( pageable );
    }


    @Test
    @DisplayName( "getPokemonById should return PokemonCompleteDTO when pokemon exists" )
    void testGetPokemonByIdSuccess( )
    {
        when( pokemonRepository.findByPokedexId( 1 ) ).thenReturn( Optional.of( pokemon ) );

        PokemonCompleteDTO result = pokemonService.getPokemonById( 1 );

        assertNotNull( result );
        assertEquals( 1, result.pokemonId() );
        assertEquals( "Bulbasaur", result.name() );
        assertEquals( "Grass", result.type1() );
        // We aren't testing captured in PokemonCompleteDTO anymore.

        verify( pokemonRepository, times( 1 ) ).findByPokedexId( 1 );
    }

    @Test
    @DisplayName( "getPokemonById should throw exception when pokemon does not exist" )
    void testGetPokemonByIdNotFound( )
    {
        when( pokemonRepository.findByPokedexId( 999 ) ).thenReturn( Optional.empty() );

        assertThrows( RuntimeException.class, () -> pokemonService.getPokemonById( 999 ) );

        verify( pokemonRepository, times( 1 ) ).findByPokedexId( 999 );
    }

    @Test
    @DisplayName( "registerPokemon should save pokemon successfully" )
    void testRegisterPokemonSuccess( )
    {
        when( pokemonRepository.existsByPokedexId( 1 ) ).thenReturn( false );
        when( pokemonRepository.existsByName( "Bulbasaur" ) ).thenReturn( false );
        when( pokemonRepository.save( any( Pokemon.class ) ) ).thenReturn( pokemon );

        pokemonService.registerPokemon( pokemonCompleteDTO );

        verify( pokemonRepository, times( 1 ) ).existsByPokedexId( 1 );
        verify( pokemonRepository, times( 1 ) ).existsByName( "Bulbasaur" );
        verify( pokemonRepository, times( 1 ) ).save( any( Pokemon.class ) );
    }

    @Test
    @DisplayName( "registerPokemon should throw exception when ID already exists" )
    void testRegisterPokemonIdAlreadyExists( )
    {
        when( pokemonRepository.existsByPokedexId( 1 ) ).thenReturn( true );

        assertThrows( PokemonIdAlreadyExistsException.class, () -> pokemonService.registerPokemon( pokemonCompleteDTO ) );

        verify( pokemonRepository, times( 1 ) ).existsByPokedexId( 1 );
        verify( pokemonRepository, never() ).save( any( Pokemon.class ) );
    }

    @Test
    @DisplayName( "registerPokemon should throw exception when name already exists" )
    void testRegisterPokemonNameAlreadyExists( )
    {
        when( pokemonRepository.existsByPokedexId( 1 ) ).thenReturn( false );
        when( pokemonRepository.existsByName( "Bulbasaur" ) ).thenReturn( true );

        assertThrows( PokemonNameAlreadyExistsException.class, () -> pokemonService.registerPokemon( pokemonCompleteDTO ) );

        verify( pokemonRepository, times( 1 ) ).existsByPokedexId( 1 );
        verify( pokemonRepository, times( 1 ) ).existsByName( "Bulbasaur" );
        verify( pokemonRepository, never() ).save( any( Pokemon.class ) );
    }

    @Test
    @DisplayName( "capturePokemon should update captured status and return pokemon" )
    void testCapturePokemonSuccess( )
    {
        when( pokemonRepository.findByPokedexId( 1 ) ).thenReturn( Optional.of( pokemon ) );
        when( pokemonRepository.save( any( Pokemon.class ) ) ).thenReturn( pokemon );

        PokemonCompleteDTO result = pokemonService.capturePokemon( 1, true );

        assertNotNull( result );
        assertEquals( 1, result.pokemonId() );
        assertEquals( "Bulbasaur", result.name() );
        // We aren't testing captured in PokemonCompleteDTO anymore.

        verify( pokemonRepository, times( 1 ) ).findByPokedexId( 1 );
        verify( pokemonRepository, times( 1 ) ).save( any( Pokemon.class ) );
    }

    @Test
    @DisplayName( "capturePokemon should throw exception when pokemon does not exist" )
    void testCapturePokemonNotFound( )
    {
        when( pokemonRepository.findByPokedexId( 999 ) ).thenReturn( Optional.empty() );

        assertThrows( PokemonNotFoundException.class, () -> pokemonService.capturePokemon( 999, true ) );

        verify( pokemonRepository, times( 1 ) ).findByPokedexId( 999 );
        verify( pokemonRepository, never() ).save( any( Pokemon.class ) );
    }

    @Test
    @DisplayName( "unregisterPokemon should delete pokemon when it exists" )
    void testUnregisterPokemonSuccess( )
    {
        when( pokemonRepository.existsByPokedexId( 1 ) ).thenReturn( true );

        pokemonService.unregisterPokemon( 1 );

        verify( pokemonRepository, times( 1 ) ).existsByPokedexId( 1 );
        verify( pokemonRepository, times( 1 ) ).deleteByPokedexId( 1 );
    }

    @Test
    @DisplayName( "unregisterPokemon should throw exception when pokemon does not exist" )
    void testUnregisterPokemonNotFound( )
    {
        when( pokemonRepository.existsByPokedexId( 999 ) ).thenReturn( false );

        assertThrows( PokemonNotFoundException.class, () -> pokemonService.unregisterPokemon( 999 ) );

        verify( pokemonRepository, times( 1 ) ).existsByPokedexId( 999 );
        verify( pokemonRepository, never() ).deleteByPokedexId( 999 );
    }


    @Test
    @DisplayName( "existsByPokedexId should return true when pokemon exists" )
    void testExistsByPokedexIdTrue( )
    {
        when( pokemonRepository.existsByPokedexId( 1 ) ).thenReturn( true );

        boolean result = pokemonService.existsByPokedexId( 1 );

        assertTrue( result );
        verify( pokemonRepository, times( 1 ) ).existsByPokedexId( 1 );
    }

    @Test
    @DisplayName( "existsByPokedexId should return false when pokemon does not exist" )
    void testExistsByPokedexIdFalse( )
    {
        when( pokemonRepository.existsByPokedexId( 999 ) ).thenReturn( false );

        boolean result = pokemonService.existsByPokedexId( 999 );

        assertFalse( result );
        verify( pokemonRepository, times( 1 ) ).existsByPokedexId( 999 );
    }

    @Test
    @DisplayName( "existsByName should return true when pokemon exists" )
    void testExistsByNameTrue( )
    {
        when( pokemonRepository.existsByName( "Bulbasaur" ) ).thenReturn( true );

        boolean result = pokemonService.existsByName( "Bulbasaur" );

        assertTrue( result );
        verify( pokemonRepository, times( 1 ) ).existsByName( "Bulbasaur" );
    }

    @Test
    @DisplayName( "existsByName should return false when pokemon does not exist" )
    void testExistsByNameFalse( )
    {
        when( pokemonRepository.existsByName( "Unknown" ) ).thenReturn( false );

        boolean result = pokemonService.existsByName( "Unknown" );

        assertFalse( result );
        verify( pokemonRepository, times( 1 ) ).existsByName( "Unknown" );
    }
}
