package albprojects.pokedex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class PokedexApplication {

	public static void main( String[] args ) {
		SpringApplication.run( PokedexApplication.class, args );
	}

}
