package albprojects.pokedex.integration.pokeapi;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class PokeApiClientConfig {

    @Bean
    public RestClient pokeApiRestClient( PokeApiProperties properties ) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
            .withConnectTimeout( Duration.ofMillis( properties.connectTimeoutMs() ) )
            .withReadTimeout( Duration.ofMillis( properties.readTimeoutMs() ) );

        return RestClient.builder()
            .baseUrl( properties.baseUrl() )
            .requestFactory( ClientHttpRequestFactoryBuilder.detect().build( settings ) )
            .build();
    }
}