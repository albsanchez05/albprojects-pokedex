package albprojects.pokedex.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

// Service responsible for generating and validating JWT tokens.
@Service
public class JwtService
{
    // Secret key for signing JWTs, injected from application properties.
    @Value( "${security.jwt.secret-key}" )
    private String secretKey;

    // Token expiration time in milliseconds, injected from application properties.
    @Value( "${security.jwt.expiration-ms}" )
    private long jwtExpirationMs;

    // Issuer of the JWT, injected from application properties.
    @Value( "${security.jwt.issuer}" )
    private String issuer;

    // Extract username (subject) from token.
    public String extractUsername( String token )
    {
        // The subject of the JWT is typically the username of the authenticated user.
        // The extractClaim method is a generic method that takes a function to specify which claim to extract from the token.
        // Claims::getSubject is a method reference that tells the extractClaim method to extract the "subject" claim from the token, which contains the username.
        return extractClaim( token, Claims::getSubject );
    }

    // Generate a signed JWT token for a user.
    public String generateToken( UserDetails userDetails )
    {
        Date now = new Date();
        Date expiration = new Date( now.getTime() + jwtExpirationMs );

        // The Jwts.builder() is used to create a new JWT token. We set the subject (username), issuer, issued at time, expiration time, and sign the token with the secret key.
        return Jwts.builder()
            .subject( userDetails.getUsername() )
            .issuer( issuer )
            .issuedAt( now )
            .expiration( expiration )
            .signWith( getSignInKey() )
            // .compact() serializes it into the compact string format (xxxxx.yyyyy.zzzzz) that you will see in the Authorization header.
            .compact();
    }

    // Validate token signature, expiration, and ownership.
    public boolean isTokenValid( String token, UserDetails userDetails )
    {
        // Check if the username extracted from the token matches the username of the provided UserDetails and that the token is not expired.
        String username = extractUsername( token );
        return username.equals( userDetails.getUsername() ) && !isTokenExpired( token );
    }

    // Generic claim extractor.
    // This method takes a JWT token and a function that specifies which claim to extract from the token. It first extracts all claims from the token and then applies the provided function to retrieve the specific claim.
    public <T> T extractClaim( String token, Function<Claims, T> claimsResolver )
    {
        Claims claims = extractAllClaims( token );
        return claimsResolver.apply( claims );
    }

    // Check if the token has expired by comparing the expiration date in the token with the current date.
    private boolean isTokenExpired( String token )
    {
        Date expiration = extractClaim( token, Claims::getExpiration );
        return expiration.before( new Date() );
    }

    // Extract all claims from the token. This method parses the JWT token using the secret key to verify its signature and then retrieves the claims (payload) contained in the token.
    private Claims extractAllClaims( String token )
    {
        return Jwts.parser()
            .verifyWith( getSignInKey() )
            .build()
            .parseSignedClaims( token )
            .getPayload();
    }

    // Convert the secret key string into a SecretKey object that can be used for signing and verifying JWTs. The secret key is typically a long random string that should be kept secure.
    private SecretKey getSignInKey()
    {
        byte[] keyBytes = secretKey.getBytes( StandardCharsets.UTF_8 );
        return Keys.hmacShaKeyFor( keyBytes );
    }
}
