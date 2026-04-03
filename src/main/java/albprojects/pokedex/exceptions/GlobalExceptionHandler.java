package albprojects.pokedex.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler( PokemonNotFoundException.class )
    public ResponseEntity<Map<String, Object>> handlePokemonNotFound( PokemonNotFoundException e, WebRequest request )
    {
        Map<String, Object> errorResponse = Map.of(
                "timestamp", Instant.now(),
                "status", 404,
                "error", "Not Found",
                "message", e.getMessage(),
                "path", request.getDescription( false ).replace( "uri=", "" )
        );
        return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( errorResponse );
    }

    @ExceptionHandler( PokemonIdAlreadyExistsException.class )
    public ResponseEntity<Map<String, Object>> handlePokemonIdAlreadyExists( PokemonIdAlreadyExistsException e, WebRequest request )
    {
        Map<String, Object> errorResponse = Map.of(
                "timestamp", Instant.now(),
                "status", 400,
                "error", "Bad Request",
                "message", e.getMessage(),
                "path", request.getDescription( false ).replace( "uri=", "" )
        );
        return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( errorResponse );
    }

    @ExceptionHandler( PokemonNameAlreadyExistsException.class )
    public ResponseEntity<Map<String, Object>> handlePokemonNameAlreadyExists( PokemonNameAlreadyExistsException e, WebRequest request )
    {
        Map<String, Object> errorResponse = Map.of(
                "timestamp", Instant.now(),
                "status", 400,
                "error", "Bad Request",
                "message", e.getMessage(),
                "path", request.getDescription( false ).replace( "uri=", "" )
        );
        return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( errorResponse );
    }

    @ExceptionHandler( PokemonNotCapturedException.class )
    public ResponseEntity<Map<String, Object>> handlePokemonNotCaptured( PokemonNotCapturedException e, WebRequest request )
    {
        Map<String, Object> errorResponse = Map.of(
                "timestamp", Instant.now(),
                "status", 404,
                "error", "Not Found",
                "message", e.getMessage(),
                "path", request.getDescription( false ).replace( "uri=", "" )
        );
        return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( errorResponse );
    }

}
