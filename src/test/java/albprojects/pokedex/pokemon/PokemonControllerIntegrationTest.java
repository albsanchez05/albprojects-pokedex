package albprojects.pokedex.pokemon;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles( "test" )
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
        // Apply the full Spring Security filter chain so auth is enforced.
        mockMvc = MockMvcBuilders
            .webAppContextSetup( webApplicationContext )
            .apply( springSecurity() )
            .build();

        objectMapper = new ObjectMapper();
        pokemonRepository.deleteAll(); // Clean the database before every test
    }

    @Test
    @DisplayName( "POST /pokemons should register a new pokemon successfully" )
    @WithMockUser( roles = "ADMIN" ) // Simulates an authenticated ADMIN user without a real JWT
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
    @WithMockUser( roles = "ADMIN" )
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
    @WithMockUser( roles = "ADMIN" )
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
    @WithMockUser( roles = "USER" ) // Read-only access, USER role is sufficient
    void testGetPokemonByIdSuccess( ) throws Exception
    {
        // Seed a Pokemon directly via repository to avoid needing ADMIN role in this test.
        pokemonRepository.deleteAll();

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

        // Register using a temporary ADMIN mock to set up the data.
        mockMvc.perform( post( "/api/pokemons" )
                        .with( org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user( "admin" ).roles( "ADMIN" ) )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() );

        // Retrieve with USER role.
        mockMvc.perform( get( "/api/pokemons/1" ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.pokemonId" ).value( 1 ) )
                .andExpect( jsonPath( "$.name" ).value( "Bulbasaur" ) )
                .andExpect( jsonPath( "$.type1" ).value( "Grass" ) )
                .andExpect( jsonPath( "$.captured" ).value( false ) );
    }

    @Test
    @DisplayName( "GET /pokemons/{pokedexId} should return not found when pokemon does not exist" )
    @WithMockUser( roles = "USER" )
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
    @WithMockUser( roles = "USER" )
    void testCapturePokemonSuccess( ) throws Exception
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
                true
        );

        mockMvc.perform( post( "/api/pokemons" )
                        .with( org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user( "admin" ).roles( "ADMIN" ) )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( pokemonDTO ) ) )
                .andExpect( status().isOk() );

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
    @WithMockUser( roles = "USER" )
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
    @WithMockUser( roles = "ADMIN" )
    void testUnregisterPokemonSuccess( ) throws Exception
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

        mockMvc.perform( delete( "/api/pokemons/1" ) )
                .andExpect( status().isNoContent() );

        // Verify that the Pokemon no longer exists.
        assertFalse( pokemonRepository.existsByPokedexId( 1 ) );
    }
}
