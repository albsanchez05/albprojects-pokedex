package albprojects.pokedex.integration.pokeapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( prefix = "integration.pokeapi" )
public record PokeApiProperties(
    String baseUrl,
    int connectTimeoutMs,
    int readTimeoutMs,
    boolean enabled
) {}