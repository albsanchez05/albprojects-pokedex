package albprojects.pokedex;

import albprojects.pokedex.pokemon.dto.PokemonCompleteDTO;
import albprojects.pokedex.pokemon.dto.PokemonCaptureDTO;
import albprojects.pokedex.pokemon.repository.PokemonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@DisplayName( "PokemonController Integration Tests" )
class PokemonControllerIntegrationTest
{
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PokemonRepository pokemonRepository;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp( )
    {
        mockMvc = MockMvcBuilders.webAppContextSetup( webApplicationContext ).build();
        objectMapper = new ObjectMapper();
        pokemonRepository.deleteAll(); // Clean the database before every test
    }

    @Test
    @DisplayName( "POST /pokemons should register a new pokemon successfully" )
    void testRegisterPokemonSuccess( ) throws Exception
    {
        PokemonCompleteDTO pokemonDTO = new PokemonCompleteDTO(
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

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() );

        // Verify that the Pokemon was saved in the database
        assertTrue( pokemonRepository.existsByPokedexId( 1 ) );
    }

    @Test
    @DisplayName( "POST /pokemons should return bad request when ID already exists" )
    void testRegisterPokemonIdAlreadyExists( ) throws Exception
    {
        // First register a Pokemon
        PokemonCompleteDTO pokemonDTO = new PokemonCompleteDTO(
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

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() );

        // Try to register the same Pokemon again
        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$.status" ).value( 400 ) )
                .andExpect( jsonPath( "$.error" ).value( "Bad Request" ) )
                .andExpect( jsonPath( "$.message" ).value( "Pokemon with this ID has already been registered" ) );
    }

    @Test
    @DisplayName( "GET /pokemons should return a page of pokemons" )
    void testGetAllPokemons( ) throws Exception
    {
        // Register multiple Pokemons
        PokemonCompleteDTO bulbasaur = new PokemonCompleteDTO(
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

        PokemonCompleteDTO charmander = new PokemonCompleteDTO(
                4,
                "Charmander",
                "Fire",
                null,
                39,
                52,
                43,
                60,
                50,
                65,
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png",
                false
        );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( bulbasaur ) ) )
                .andExpect( status().isOk() );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( charmander ) ) )
                .andExpect( status().isOk() );

        // Get all Pokemons
        mockMvc.perform( get( "/api/pokemons" )
                        .param( "page", "0" )
                        .param( "size", "10" ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.content", hasSize( 2 ) ) )
                .andExpect( jsonPath( "$.content[0].name" ).value( "Bulbasaur" ) )
                .andExpect( jsonPath( "$.content[1].name" ).value( "Charmander" ) );
    }

    @Test
    @DisplayName( "GET /pokemons/{pokedexId} should return pokemon when exists" )
    void testGetPokemonByIdSuccess( ) throws Exception
    {
        // Registrar un Pokemon
        PokemonCompleteDTO pokemonDTO = new PokemonCompleteDTO(
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

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() );

        // Obtener por ID
        mockMvc.perform( get( "/api/pokemons/1" ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.pokemonId" ).value( 1 ) )
                .andExpect( jsonPath( "$.name" ).value( "Bulbasaur" ) )
                .andExpect( jsonPath( "$.type1" ).value( "Grass" ) )
                .andExpect( jsonPath( "$.captured" ).value( false ) );
    }

    @Test
    @DisplayName( "GET /pokemons/{pokedexId} should return not found when pokemon does not exist" )
    void testGetPokemonByIdNotFound( ) throws Exception
    {
        mockMvc.perform( get( "/api/pokemons/2" ) )
                .andExpect( status().isNotFound() )
                .andExpect( jsonPath( "$.status" ).value( 404 ) )
                .andExpect( jsonPath( "$.error" ).value( "Not Found" ) )
                .andExpect( jsonPath( "$.message" ).value( "Pokemon not found with id: 2" ) );
    }

    @Test
    @DisplayName( "POST /pokemons/{id} should update captured status successfully" )
    void testCapturePokemonSuccess( ) throws Exception
    {
        // Registrar un Pokemon
        PokemonCompleteDTO pokemonDTO = new PokemonCompleteDTO(
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
                true
        );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() );

        // Capturar el Pokemon
        PokemonCaptureDTO captureDTO = new PokemonCaptureDTO( 1, true );

        mockMvc.perform( post( "/api/pokemons/1" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( captureDTO ) ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.pokemonId" ).value( 1 ) )
                .andExpect( jsonPath( "$.name" ).value( "Bulbasaur" ) )
                .andExpect( jsonPath( "$.captured" ).value( true ) );
    }

    @Test
    @DisplayName( "POST /pokemons/{id} should return not found when pokemon does not exist" )
    void testCapturePokemonNotFound( ) throws Exception
    {
        PokemonCaptureDTO captureDTO = new PokemonCaptureDTO( 999, true );

        mockMvc.perform( post( "/api/pokemons/999" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( captureDTO ) ) )
                .andExpect( status().isNotFound() )
                .andExpect( jsonPath( "$.status" ).value( 404 ) )
                .andExpect( jsonPath( "$.error" ).value( "Not Found" ) )
                .andExpect( jsonPath( "$.message" ).value( "Pokemon not found with id: 999" ) );
    }

    @Test
    @DisplayName( "DELETE /pokemons/{pokedexId} should unregister pokemon successfully and return 204" )
    void testUnregisterPokemonSuccess( ) throws Exception
    {
        // Registrar un Pokemon
        PokemonCompleteDTO pokemonDTO = new PokemonCompleteDTO(
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

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() );

        // Liberar el Pokemon
        mockMvc.perform( delete( "/api/pokemons/1" ) )
                .andExpect( status().isNoContent() );

        // Verificar que ya no existe
        assertFalse( pokemonRepository.existsByPokedexId( 1 ) );
    }
}
