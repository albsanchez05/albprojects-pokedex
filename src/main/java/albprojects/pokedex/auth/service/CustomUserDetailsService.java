package albprojects.pokedex.auth.service;

import albprojects.pokedex.auth.model.User;
import albprojects.pokedex.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// This class is responsible for loading user-specific data during the authentication process.
// UserDetailsService is an interface provided by Spring Security that defines a method to retrieve user details based on the username.
@Service
public class CustomUserDetailsService
    implements UserDetailsService
{
    private final UserRepository userRepository;

    // Constructor to inject the UserRepository dependency.
    public CustomUserDetailsService( UserRepository userRepository )
    {
        this.userRepository = userRepository;
    }

    // This method retrieves a user from the database using the provided username.
    // If the user is not found, it throws a UsernameNotFoundException.
    @Override
    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException
    {
        //  Fetch the user from the database using the UserRepository.
        User user = userRepository.findByUsername( username )
                .orElseThrow( () -> new UsernameNotFoundException( "User not found: " + username ) );

        // Spring Security expects role authorities with ROLE_ prefix.
        // SimpleGrantedAuthority is a simple implementation of the GrantedAuthority interface, which represents an authority granted to the user.
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority( "ROLE_" + user.getRole().name() )
        );

        // Return a UserDetails object containing the user's information and authorities.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
