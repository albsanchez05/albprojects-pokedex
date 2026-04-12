package albprojects.pokedex.auth.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table (
    name = "app_user",
    uniqueConstraints = {
        @UniqueConstraint( name = "uk_app_user_username", columnNames = "username" ),
        @UniqueConstraint( name = "uk_app_user_email", columnNames = "email" )
    }
)
public class User
{
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column ( nullable = false, length = 50 )
    private String username;

    @Column ( nullable = false, length = 100 )
    private String email;

    @Column ( nullable = false, length = 255 )
    private String password;

    @Enumerated( EnumType.STRING )
    @Column ( nullable = false, length =  20 )
    private Role role;
}
