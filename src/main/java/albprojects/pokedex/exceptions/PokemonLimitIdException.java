package albprojects.pokedex.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( HttpStatus.BAD_REQUEST )
public class PokemonLimitIdException extends RuntimeException
{
    public PokemonLimitIdException(String message )
    {
        super( message );
    }
}
