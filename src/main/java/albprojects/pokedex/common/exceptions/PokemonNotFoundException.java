package albprojects.pokedex.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( HttpStatus.NOT_FOUND )
public class PokemonNotFoundException extends RuntimeException
{
    public PokemonNotFoundException( String message )
    {
        super( message );
    }
}
