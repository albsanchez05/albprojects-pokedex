package albprojects.pokedex.repository;

import albprojects.pokedex.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Repository interface for Pokemon entities, extending JpaRepository to provide CRUD operations

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    boolean existsByPokedexId( Integer pokedexId );
    boolean existsByName( String name );
    void deleteByPokedexId( Integer pokedexId );
    Optional<Pokemon> findByPokedexId( Integer pokedexId );
}
