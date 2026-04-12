package albprojects.pokedex.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( HttpStatus.BAD_REQUEST )
public class PokemonIdAlreadyExistsException extends RuntimeException
{
    public PokemonIdAlreadyExistsException( String message )
    {
        super( message );
    }
}
