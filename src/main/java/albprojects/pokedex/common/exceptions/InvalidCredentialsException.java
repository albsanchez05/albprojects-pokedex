package albprojects.pokedex.common.exceptions;

// Thrown when the provided credentials ( username / password ) do not match any registered user.
public class InvalidCredentialsException extends RuntimeException
{
    public InvalidCredentialsException( String message )
    {
        super( message );
    }
}

