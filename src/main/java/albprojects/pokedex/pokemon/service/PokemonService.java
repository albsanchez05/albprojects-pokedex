package albprojects.pokedex.pokemon.service;

import albprojects.pokedex.common.exceptions.PageNotFoundException;
import albprojects.pokedex.common.exceptions.PokemonIdAlreadyExistsException;
import albprojects.pokedex.common.exceptions.PokemonNameAlreadyExistsException;
import albprojects.pokedex.common.exceptions.PokemonNotFoundException;
import albprojects.pokedex.pokemon.dto.PokemonBriefDTO;
import albprojects.pokedex.pokemon.dto.PokemonCompleteDTO;
import albprojects.pokedex.pokemon.model.Pokemon;
import albprojects.pokedex.pokemon.repository.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PokemonService {
    @Autowired
    PokemonRepository pokemonRepository;

    public Page<PokemonBriefDTO> getAllPokemons( Pageable pageable ) {
        Page<Pokemon> pokemonPage = pokemonRepository.findAllByOrderByPokedexIdAsc( pageable );
        if ( pokemonPage.isEmpty() && pageable.getPageNumber() > 0 ) {
            throw new PageNotFoundException( "The requested page does not exist." );
        }
        return pokemonPage.map( pokemon -> new PokemonBriefDTO( pokemon.getPokedexId(), pokemon.getName(), pokemon.getImage() ) );
    }

    public PokemonCompleteDTO getPokemonById( Integer pokedexId ) {
        return pokemonRepository.findByPokedexId( pokedexId )
                .map( pokemon -> new PokemonCompleteDTO(
                        pokemon.getPokedexId(),
                        pokemon.getName(),
                        pokemon.getType1(),
                        pokemon.getType2(),
                        pokemon.getHp(),
                        pokemon.getAttack(),
                        pokemon.getDefense(),
                        pokemon.getSpAttack(),
                        pokemon.getSpDefense(),
                        pokemon.getSpeed(),
                        pokemon.getImage(),
                        pokemon.isCaptured()
                ) )
                .orElseThrow( () -> new PokemonNotFoundException( "Pokemon not found with id: " + pokedexId ) );
    }

    @Transactional
    public PokemonCompleteDTO registerPokemon( PokemonCompleteDTO pokemonCompleteDTO )
    {
        Integer pokedexId = pokemonCompleteDTO.pokemonId();
        if( pokemonRepository.existsByPokedexId( pokedexId ) )
        {
            throw new PokemonIdAlreadyExistsException( "Pokemon with this ID has already been registered" );
        }
        if( pokemonRepository.existsByName( pokemonCompleteDTO.name() ) )
        {
            throw new PokemonNameAlreadyExistsException( "Pokemon with this name has already been registered" );
        }

        Pokemon pokemon = new Pokemon();

        pokemon.setPokedexId( pokemonCompleteDTO.pokemonId() );
        pokemon.setName( pokemonCompleteDTO.name() );
        pokemon.setType1( pokemonCompleteDTO.type1() );
        pokemon.setType2( pokemonCompleteDTO.type2() );
        pokemon.setHp( pokemonCompleteDTO.hp() );
        pokemon.setAttack( pokemonCompleteDTO.attack() );
        pokemon.setDefense( pokemonCompleteDTO.defense() );
        pokemon.setSpAttack( pokemonCompleteDTO.spAttack() );
        pokemon.setSpDefense( pokemonCompleteDTO.spDefense() );
        pokemon.setSpeed( pokemonCompleteDTO.speed() );
        pokemon.setImage( pokemonCompleteDTO.image() );
        pokemon.setCaptured( false ); // Always set to false on registration

        pokemonRepository.save( pokemon );

        return new PokemonCompleteDTO(
                pokemon.getPokedexId(),
                pokemon.getName(),
                pokemon.getType1(),
                pokemon.getType2(),
                pokemon.getHp(),
                pokemon.getAttack(),
                pokemon.getDefense(),
                pokemon.getSpAttack(),
                pokemon.getSpDefense(),
                pokemon.getSpeed(),
                pokemon.getImage(),
                pokemon.isCaptured()
        );
    }

    @Transactional
    public PokemonCompleteDTO capturePokemon( Integer pokedexId, Boolean captured )
    {
        Pokemon pokemon = pokemonRepository.findByPokedexId( pokedexId )
                .orElseThrow( () -> new PokemonNotFoundException( "Pokemon not found with id: " + pokedexId ) );

        pokemon.setCaptured( captured );
        pokemonRepository.save( pokemon );

        return new PokemonCompleteDTO(
                pokemon.getPokedexId(),
                pokemon.getName(),
                pokemon.getType1(),
                pokemon.getType2(),
                pokemon.getHp(),
                pokemon.getAttack(),
                pokemon.getDefense(),
                pokemon.getSpAttack(),
                pokemon.getSpDefense(),
                pokemon.getSpeed(),
                pokemon.getImage(),
                pokemon.isCaptured()
        );
    }

    @Transactional
    public void unregisterPokemon( Integer pokedexId ) {
        pokemonRepository.deleteByPokedexId( pokedexId );
    }

    public boolean existsByPokedexId( Integer pokedexId ) {
        return pokemonRepository.existsByPokedexId( pokedexId );
    }
    public boolean existsByName( String name ) {
        return pokemonRepository.existsByName( name );
    }
}
