package albprojects.pokedex.integration.pokeapi;

import albprojects.pokedex.common.exceptions.ExternalIntegrationException;
import albprojects.pokedex.integration.pokeapi.dto.PokeApiPokemonResponse;
import albprojects.pokedex.pokemon.dto.PokemonCompleteDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import javax.net.ssl.SSLException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

@Service
public class PokeApiClientService
{
    private final RestClient pokeApiRestClient;
    private final PokeApiPokemonMapper mapper;
    private final PokeApiProperties properties;

    public PokeApiClientService(
        @Qualifier( "pokeApiRestClient" ) RestClient pokeApiRestClient,
        PokeApiPokemonMapper mapper,
        PokeApiProperties properties
    )
    {
        this.pokeApiRestClient = pokeApiRestClient;
        this.mapper = mapper;
        this.properties = properties;
    }

    public PokemonCompleteDTO fetchPokemonByIdOrName( String idOrName )
    {
        if( !properties.enabled() )
        {
            throw new ExternalIntegrationException(
                "External Pokemon integration is disabled",
                HttpStatus.SERVICE_UNAVAILABLE
            );
        }

        String normalizedIdOrName = idOrName == null ? "" : idOrName.trim().toLowerCase();

        try
        {
            PokeApiPokemonResponse response = pokeApiRestClient.get()
                .uri( "/pokemon/{idOrName}", normalizedIdOrName )
                .retrieve()
                .body( PokeApiPokemonResponse.class );

            if( response == null )
            {
                throw new ExternalIntegrationException(
                    "External Pokemon service returned an empty response",
                    HttpStatus.BAD_GATEWAY
                );
            }

            return mapper.toPokemonCompleteDTO( response );
        }
        catch( HttpClientErrorException.NotFound ex )
        {
            throw new ExternalIntegrationException(
                "Pokemon not found in external source: " + normalizedIdOrName,
                HttpStatus.NOT_FOUND,
                ex
            );
        }
        catch( ResourceAccessException ex )
        {
            Throwable rootCause = getRootCause( ex );

            if( rootCause instanceof SSLException )
            {
                throw new ExternalIntegrationException(
                    "External Pokemon TLS/certificate issue: " + rootCause.getMessage(),
                    HttpStatus.BAD_GATEWAY,
                    ex
                );
            }

            if( rootCause instanceof UnknownHostException )
            {
                throw new ExternalIntegrationException(
                    "External Pokemon DNS resolution failed: " + rootCause.getMessage(),
                    HttpStatus.BAD_GATEWAY,
                    ex
                );
            }

            if( rootCause instanceof SocketTimeoutException || rootCause instanceof ConnectException )
            {
                throw new ExternalIntegrationException(
                    "External Pokemon service timeout or network issue",
                    HttpStatus.GATEWAY_TIMEOUT,
                    ex
                );
            }

            throw new ExternalIntegrationException(
                "External Pokemon network access failed: " + rootCause.getMessage(),
                HttpStatus.BAD_GATEWAY,
                ex
            );
        }
        catch( HttpStatusCodeException ex )
        {
            throw new ExternalIntegrationException(
                "External Pokemon service error: HTTP " + ex.getStatusCode().value(),
                HttpStatus.BAD_GATEWAY,
                ex
            );
        }
        catch( RestClientException ex )
        {
            throw new ExternalIntegrationException(
                "Unexpected error while calling external Pokemon service",
                HttpStatus.BAD_GATEWAY,
                ex
            );
        }
    }

    private Throwable getRootCause( Throwable throwable )
    {
        Throwable current = throwable;
        while( current.getCause() != null && current.getCause() != current )
        {
            current = current.getCause();
        }
        return current;
    }
}
