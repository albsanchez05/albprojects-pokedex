package albprojects.pokedex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
            .andExpect( status().isOk() )
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
            .andExpect( status().isOk() );

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
            .andExpect( status().isForbidden() );

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
            .andExpect( status().isOk() )
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
}
