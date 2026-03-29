package albprojects.pokedex;

import albprojects.pokedex.dto.PokemonBriefDTO;
import albprojects.pokedex.dto.PokemonCompleteDTO;
import albprojects.pokedex.dto.PokemonCaptureDTO;
import albprojects.pokedex.repository.PokemonRepository;
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

import java.util.Arrays;
import java.util.List;

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
        pokemonRepository.deleteAll(); // Limpiar la base de datos antes de cada test
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
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
        );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() )
                .andExpect( content().string( "Pokemon registered successfully" ) );

        // Verificar que se guardó en la base de datos
        assertTrue( pokemonRepository.existsByPokedexId( 1 ) );
    }

    @Test
    @DisplayName( "POST /pokemons should return bad request when ID already exists" )
    void testRegisterPokemonIdAlreadyExists( ) throws Exception
    {
        // Primero registrar un Pokemon
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
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
        );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() );

        // Intentar registrar el mismo ID
        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$.status" ).value( 400 ) )
                .andExpect( jsonPath( "$.error" ).value( "Bad Request" ) )
                .andExpect( jsonPath( "$.message" ).value( "Pokemon with this ID has already been captured" ) );
    }

    @Test
    @DisplayName( "GET /pokemons should return a page of pokemons" )
    void testGetAllPokemons( ) throws Exception
    {
        // Registrar algunos Pokemon primero
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
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
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
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"
        );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( bulbasaur ) ) )
                .andExpect( status().isOk() );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( charmander ) ) )
                .andExpect( status().isOk() );

        // Obtener todos
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
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
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
                .andExpect( jsonPath( "$.type1" ).value( "Grass" ) );
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
    @DisplayName( "GET /pokemons/{pokedexId} should return bad request when ID is out of range" )
    void testGetPokemonByIdOutOfRange( ) throws Exception
    {
        mockMvc.perform( get( "/api/pokemons/999" ) )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$.status" ).value( 400 ) )
                .andExpect( jsonPath( "$.error" ).value( "Bad Request" ) )
                .andExpect( jsonPath( "$.message" ).value( "ID must be between 1 and 151" ) );
    }

    @Test
    @DisplayName( "POST /pokemons/capture should return pokemon when both ID and name match" )
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
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
        );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() );

        // Capturar el Pokemon
        PokemonCaptureDTO captureDTO = new PokemonCaptureDTO( 1, "Bulbasaur" );

        mockMvc.perform( post( "/api/pokemons/capture" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( captureDTO ) ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.pokemonId" ).value( 1 ) )
                .andExpect( jsonPath( "$.name" ).value( "Bulbasaur" ) );
    }

    @Test
    @DisplayName( "POST /pokemons/capture should return not found when pokemon is not registered" )
    void testCapturePokemonNotRegistered( ) throws Exception
    {
        PokemonCaptureDTO captureDTO = new PokemonCaptureDTO( 999, "Unknown" );

        mockMvc.perform( post( "/api/pokemons/capture" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( captureDTO ) ) )
                .andExpect( status().isNotFound() )
                .andExpect( jsonPath( "$.status" ).value( 404 ) )
                .andExpect( jsonPath( "$.error" ).value( "Not Found" ) )
                .andExpect( jsonPath( "$.message" ).value( "Pokemon not registered yet" ) );
    }
    
    @Test
    @DisplayName( "DELETE /pokemons/{pokedexId} should release pokemon successfully" )
    void testReleasePokemonSuccess( ) throws Exception
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
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
        );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() );

        // Liberar el Pokemon
        mockMvc.perform( delete( "/api/pokemons/1" ) )
                .andExpect( status().isOk() )
                .andExpect( content().string( "Pokemon N. 1 has been released successfully" ) );

        // Verificar que ya no existe
        assertFalse( pokemonRepository.existsByPokedexId( 1 ) );
    }

    @Test
    @DisplayName( "DELETE /pokemons should release all pokemons" )
    void testReleaseAllPokemons( ) throws Exception
    {
        // Registrar algunos Pokemon
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
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
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
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"
        );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( bulbasaur ) ) )
                .andExpect( status().isOk() );

        mockMvc.perform( post( "/api/pokemons" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( charmander ) ) )
                .andExpect( status().isOk() );

        // Liberar todos
        mockMvc.perform( delete( "/api/pokemons" ) )
                .andExpect( status().isOk() )
                .andExpect( content().string( "All pokemons have been released successfully" ) );

        // Verificar que no quedan Pokemon
        assertEquals( 0, pokemonRepository.count() );
    }
}
