package albprojects.pokedex.common.exceptions;

import org.springframework.http.HttpStatus;

public class ExternalIntegrationException extends RuntimeException
{
    private final HttpStatus status;

    public ExternalIntegrationException( String message, HttpStatus status )
    {
        super( message );
        this.status = status;
    }

    public ExternalIntegrationException( String message, HttpStatus status, Throwable cause )
    {
        super( message, cause );
        this.status = status;
    }

    public HttpStatus getStatus()
    {
        return status;
    }
}