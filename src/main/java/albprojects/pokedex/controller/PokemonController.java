package albprojects.pokedex.controller;

import albprojects.pokedex.dto.PokemonBriefDTO;
import albprojects.pokedex.dto.PokemonCaptureDTO;
import albprojects.pokedex.dto.PokemonCompleteDTO;
import albprojects.pokedex.exceptions.PokemonNotFoundException;
import albprojects.pokedex.service.PokemonService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api" )
public class PokemonController implements PokemonControllerAPI {

    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 5;

    @Autowired
    private PokemonService pokemonService;

    @Override
    @GetMapping( "/pokemons" )
    public Page<PokemonBriefDTO> getAllPokemons( @Parameter( hidden = true ) @PageableDefault( page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE ) Pageable pageable ) {
        return pokemonService.getAllPokemons( pageable );
    }

    @Override
    @GetMapping( "/pokemons/{pokedexId}" )
    public PokemonCompleteDTO getPokemonById( @PathVariable Integer pokedexId ) {
        if ( !pokemonService.existsByPokedexId( pokedexId ) ) {
            throw new PokemonNotFoundException( "Pokemon not found with id: " + pokedexId );
        }
        return pokemonService.getPokemonById( pokedexId );
    }

    @Override
    @PostMapping( "/pokemons/{id}" )
    public PokemonCompleteDTO capturePokemon( @RequestBody PokemonCaptureDTO pokemonCaptureDTO ) {
        return pokemonService.capturePokemon( pokemonCaptureDTO.pokedexId(), pokemonCaptureDTO.captured() );
    }

    @Override
    @PostMapping( "/pokemons" )
    public void registerPokemon( @Valid @RequestBody PokemonCompleteDTO pokemonCompleteDTO ) {
        pokemonService.registerPokemon( pokemonCompleteDTO );
    }

    @Override
    @DeleteMapping( "/pokemons/{pokedexId}" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void releasePokemon( @PathVariable Integer pokedexId ) {
        pokemonService.unregisterPokemon( pokedexId );
    }
}
