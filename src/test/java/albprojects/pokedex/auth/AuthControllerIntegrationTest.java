package albprojects.pokedex.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles( "test" )
@DisplayName( "AuthController Integration Tests" )
class AuthControllerIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName( "POST /api/auth/register should return JWT token" )
    void testRegisterReturnsToken( ) throws Exception
    {
        String unique = UUID.randomUUID().toString().substring( 0, 8 );

        String payload = """
            {
              "username": "user-%s",
              "email": "user-%s@pokedex.local",
              "password": "Pikachu@123"
            }
            """.formatted( unique, unique );

        mockMvc.perform( post( "/api/auth/register" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( payload ) )
            .andExpect( status().isCreated() )
            .andExpect( jsonPath( "$.token" ).isNotEmpty() );
    }

    @Test
    @DisplayName( "POST /api/auth/login should return JWT token after register" )
    void testLoginReturnsToken( ) throws Exception
    {
        String unique = UUID.randomUUID().toString().substring( 0, 8 );
        String username = "user-" + unique;
        String email = "user-" + unique + "@pokedex.local";
        String password = "Pikachu@123";

        String registerPayload = """
            {
              "username": "%s",
              "email": "%s",
              "password": "%s"
            }
            """.formatted( username, email, password );

        mockMvc.perform( post( "/api/auth/register" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( registerPayload ) )
            .andExpect( status().isCreated() );

        String loginPayload = """
            {
              "username": "%s",
              "password": "%s"
            }
            """.formatted( username, password );

        mockMvc.perform( post( "/api/auth/login" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( loginPayload ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.token" ).isNotEmpty() );
    }

    @Test
    @DisplayName( "GET /api/pokemons should be forbidden without token and allowed with token" )
    void testProtectedEndpointWithAndWithoutToken( ) throws Exception
    {
        mockMvc.perform( get( "/api/pokemons" ) )
            .andExpect( status().isUnauthorized() );

        String unique = UUID.randomUUID().toString().substring( 0, 8 );
        String username = "user-" + unique;
        String email = "user-" + unique + "@pokedex.local";
        String password = "Pikachu@123";

        String registerPayload = """
            {
              "username": "%s",
              "email": "%s",
              "password": "%s"
            }
            """.formatted( username, email, password );

        String registerResponse = mockMvc.perform( post( "/api/auth/register" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( registerPayload ) )
            .andExpect( status().isCreated() )
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree( registerResponse );
        String token = jsonNode.get( "token" ).asText();
        assertNotNull( token );

        mockMvc.perform( get( "/api/pokemons" )
                .header( "Authorization", "Bearer " + token ) )
            .andExpect( status().isOk() );
    }

    @Test
    @DisplayName( "GET /api/pokemons with invalid token should return 401" )
    void testProtectedEndpointWithInvalidToken( ) throws Exception
    {
        mockMvc.perform( get( "/api/pokemons" )
                .header( "Authorization", "Bearer invalid.token.value" ) )
            .andExpect( status().isUnauthorized() );
    }

    @Test
    @DisplayName( "POST /api/auth/login with wrong password should return 401" )
    void testLoginWithWrongPassword( ) throws Exception
    {
        // First register a user so the username exists.
        String unique = UUID.randomUUID().toString().substring( 0, 8 );
        String username = "user-" + unique;

        String registerPayload = """
            {
              "username": "%s",
              "email": "user-%s@pokedex.local",
              "password": "Pikachu@123"
            }
            """.formatted( username, unique );

        mockMvc.perform( post( "/api/auth/register" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( registerPayload ) )
            .andExpect( status().isCreated() );

        // Attempt login with the wrong password.
        String loginPayload = """
            {
              "username": "%s",
              "password": "WrongPass@999"
            }
            """.formatted( username );

        mockMvc.perform( post( "/api/auth/login" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( loginPayload ) )
            .andExpect( status().isUnauthorized() );
    }

    @Test
    @DisplayName( "POST /api/pokemons with USER role token should return 403" )
    void testUserRoleCannotWritePokemons( ) throws Exception
    {
        // Register a regular USER ( register always assigns the USER role ).
        String unique = UUID.randomUUID().toString().substring( 0, 8 );
        String username = "user-" + unique;

        String registerPayload = """
            {
              "username": "%s",
              "email": "user-%s@pokedex.local",
              "password": "Pikachu@123"
            }
            """.formatted( username, unique );

        String registerResponse = mockMvc.perform( post( "/api/auth/register" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( registerPayload ) )
            .andExpect( status().isCreated() )
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Extract the token issued to the regular USER.
        JsonNode jsonNode = objectMapper.readTree( registerResponse );
        String userToken = jsonNode.get( "token" ).asText();
        assertNotNull( userToken );

        // A USER trying to POST ( write ) to /api/pokemons must be rejected with 403.
        String pokemonPayload = """
            {
              "pokemonId": 1,
              "name": "Bulbasaur",
              "type1": "Grass",
              "type2": "Poison",
              "hp": 45,
              "attack": 49,
              "defense": 49,
              "spAttack": 65,
              "spDefense": 65,
              "speed": 45,
              "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
              "captured": false
            }
            """;

        mockMvc.perform( post( "/api/pokemons" )
                .header( "Authorization", "Bearer " + userToken )
                .contentType( MediaType.APPLICATION_JSON )
                .content( pokemonPayload ) )
            .andExpect( status().isForbidden() );
    }

    @Test
    @DisplayName( "POST /api/pokemons/{id} with USER role token should be authorized" )
    void testUserRoleCanCapturePokemon( ) throws Exception
    {
        String unique = UUID.randomUUID().toString().substring( 0, 8 );
        String username = "user-" + unique;

        String registerPayload = """
            {
              "username": "%s",
              "email": "user-%s@pokedex.local",
              "password": "Pikachu@123"
            }
            """.formatted( username, unique );

        String registerResponse = mockMvc.perform( post( "/api/auth/register" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( registerPayload ) )
            .andExpect( status().isCreated() )
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree( registerResponse );
        String userToken = jsonNode.get( "token" ).asText();
        assertNotNull( userToken );

        String capturePayload = """
            {
              "pokedexId": 999,
              "captured": true
            }
            """;

        // 404 confirms endpoint authorization passed and business validation handled the missing Pokemon.
        mockMvc.perform( post( "/api/pokemons/999" )
                .header( "Authorization", "Bearer " + userToken )
                .contentType( MediaType.APPLICATION_JSON )
                .content( capturePayload ) )
            .andExpect( status().isNotFound() );
    }
}
