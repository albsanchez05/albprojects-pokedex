package albprojects.pokedex.service;

import albprojects.pokedex.dto.PokemonBriefDTO;
import albprojects.pokedex.dto.PokemonCompleteDTO;
import albprojects.pokedex.exceptions.*;
import albprojects.pokedex.model.Pokemon;
import albprojects.pokedex.repository.PokemonRepository;
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
        return pokemonRepository.findAll( pageable )
                .map(pokemon -> new PokemonBriefDTO( pokemon.getPokedexId(), pokemon.getName(), pokemon.getImage() ) );
    }

    public PokemonCompleteDTO getPokemonById(Integer pokedexId) {
        return pokemonRepository.findByPokedexId(pokedexId)
                .map(pokemon -> new PokemonCompleteDTO(
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
                        pokemon.getImage()
                ))
                .orElseThrow(() -> new PokemonNotFoundException("Pokemon not found with id: " + pokedexId));
    }

    @Transactional
    public void registerPokemon( PokemonCompleteDTO pokemonCompleteDTO )
    {
        Integer pokedexId = pokemonCompleteDTO.pokemonId();
        if( pokedexId > 151 )
        {
            throw new PokemonLimitIdException( "ID cannot exceed the Pokedex limit" );
        }
        if( pokemonRepository.existsByPokedexId( pokedexId ) )
        {
            throw new PokemonIdAlreadyExistsException( "Pokemon with this ID has already been captured" );
        }
        if( pokemonRepository.existsByName( pokemonCompleteDTO.name() ) )
        {
            throw new PokemonNameAlreadyExistsException( "Pokemon with this name has already been captured" );
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

        pokemonRepository.save( pokemon );
    }

    public PokemonCompleteDTO capturePokemon(Integer pokedexId, String name) {
        if (pokemonRepository.existsByPokedexIdAndName(pokedexId, name)) {
            return getPokemonById(pokedexId);
        }
        if (pokemonRepository.existsByPokedexId(pokedexId)) {
            throw new PokemonNameAlreadyExistsException("Pokemon with this ID has already been captured");
        } else if (pokemonRepository.existsByName(name)) {
            throw new PokemonIdAlreadyExistsException("Pokemon with this name has already been captured");
        } else {
            throw new PokemonNotCapturedException("Pokemon not registered yet");
        }
    }

    @Transactional
    public void releasePokemon( Integer pokedexId ) {
        if ( !existsByPokedexId( pokedexId ) ) {
            throw new PokemonNotFoundException( "Pokemon not found with id: " + pokedexId );
        }
        pokemonRepository.deleteByPokedexId( pokedexId );
    }

    @Transactional
    public void releaseAllPokemons() {
        pokemonRepository.deleteAll();
    }

    public boolean existsByPokedexId( Integer pokedexId ) {
        return pokemonRepository.existsByPokedexId( pokedexId );
    }
    public boolean existsByName( String name ) {
        return pokemonRepository.existsByName( name );
    }
}
