package albprojects.pokedex.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
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

    @ExceptionHandler( PageNotFoundException.class )
    public ResponseEntity<Map<String, Object>> handlePageNotFound( PageNotFoundException e, WebRequest request )
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

    // Handles exceptions for invalid login credentials.
    @ExceptionHandler( InvalidCredentialsException.class )
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials( InvalidCredentialsException e, WebRequest request )
    {
        Map<String, Object> errorResponse = Map.of(
                "timestamp", Instant.now(),
                "status", 401,
                "error", "Unauthorized",
                "message", e.getMessage(),
                "path", request.getDescription( false ).replace( "uri=", "" )
        );
        return ResponseEntity.status( HttpStatus.UNAUTHORIZED ).body( errorResponse );
    }

    // Handles exceptions when a user is not found during authentication.
    @ExceptionHandler( UsernameNotFoundException.class )
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound( UsernameNotFoundException e, WebRequest request )
    {
        Map<String, Object> errorResponse = Map.of(
                "timestamp", Instant.now(),
                "status", 401,
                "error", "Unauthorized",
                "message", "Invalid credentials", // Masking the original "User not found" for security
                "path", request.getDescription( false ).replace( "uri=", "" )
        );
        return ResponseEntity.status( HttpStatus.UNAUTHORIZED ).body( errorResponse );
    }

    // Handles exceptions from @Valid validation on request bodies.
    @ExceptionHandler( MethodArgumentNotValidException.class )
    public ResponseEntity<Map<String, Object>> handleValidationExceptions( MethodArgumentNotValidException ex, WebRequest request ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach( ( error ) -> {
            String fieldName = ( (FieldError) error ).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put( fieldName, errorMessage );
        } );

        Map<String, Object> errorResponse = Map.of(
                "timestamp", Instant.now(),
                "status", 400,
                "error", "Bad Request",
                "message", "Validation failed",
                "details", errors,
                "path", request.getDescription( false ).replace( "uri=", "" )
        );
        return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( errorResponse );
    }

    // Handles exceptions when an authenticated user lacks required permissions.
    @ExceptionHandler( AccessDeniedException.class )
    public ResponseEntity<Map<String, Object>> handleAccessDenied( AccessDeniedException e, WebRequest request ) {
        Map<String, Object> errorResponse = Map.of(
                "timestamp", Instant.now(),
                "status", 403,
                "error", "Forbidden",
                "message", "You do not have permission to access this resource",
                "path", request.getDescription( false ).replace( "uri=", "" )
        );
        return ResponseEntity.status( HttpStatus.FORBIDDEN ).body( errorResponse );
    }

    // A general handler for authentication issues triggered by security filters.
    @ExceptionHandler( AuthenticationException.class )
    public ResponseEntity<Map<String, Object>> handleAuthenticationException( AuthenticationException e, WebRequest request ) {
        Map<String, Object> errorResponse = Map.of(
                "timestamp", Instant.now(),
                "status", 401,
                "error", "Unauthorized",
                "message", "Authentication failed: " + e.getMessage(),
                "path", request.getDescription( false ).replace( "uri=", "" )
        );
        return ResponseEntity.status( HttpStatus.UNAUTHORIZED ).body( errorResponse );
    }

    // Handles exceptions from external API integrations.
    @ExceptionHandler( ExternalIntegrationException.class )
    public ResponseEntity<Map<String, Object>> handleExternalIntegrationException( ExternalIntegrationException e, WebRequest request )
    {
        Map<String, Object> errorResponse = Map.of(
            "timestamp", Instant.now(),
            "status", e.getStatus().value(),
            "error", e.getStatus().getReasonPhrase(),
            "message", e.getMessage(),
            "path", request.getDescription( false ).replace( "uri=", "" )
        );
        return ResponseEntity.status( e.getStatus() ).body( errorResponse );
    }
}
