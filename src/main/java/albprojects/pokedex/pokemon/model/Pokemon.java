package albprojects.pokedex.pokemon.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode methods
@Entity // JPA annotation to indicate that this class is a database entity
@Table( name = "pokemon" ) // JPA annotation to specify the table name in the database
public class Pokemon
{
    @Id // JPA annotation to indicate that this field is the primary key
    @GeneratedValue( strategy = GenerationType.IDENTITY ) // JPA annotation to specify that the ID should be generated automatically by the database
    private Long id;

    @Column( name = "pokedex_id", nullable = false ) // JPA annotation to specify the column name and that it cannot be null
    private Integer pokedexId;

    @Column( nullable = false ) // JPA annotation to specify that this column cannot be null
    private String name;

    @Column( name = "type1", nullable = false )
    private String type1;

    private String type2;

    @Column( nullable = false )
    private Integer hp;

    @Column( nullable = false )
    private Integer attack;

    @Column( nullable = false )
    private Integer defense;

    @Column( name = "sp_attack", nullable = false ) // JPA annotation to specify the column name
    private Integer spAttack;

    @Column( name = "sp_defense", nullable = false ) // JPA annotation to specify the column name
    private Integer spDefense;

    @Column( nullable = false ) // JPA annotation to specify the column name
    private Integer speed;

    @Column( nullable = false )
    private String image;

    @Column( nullable = false )
    private boolean captured;
}
