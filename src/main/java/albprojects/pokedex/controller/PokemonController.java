package albprojects.pokedex.controller;

import albprojects.pokedex.dto.PokemonBriefDTO;
import albprojects.pokedex.dto.PokemonCompleteDTO;
import albprojects.pokedex.dto.PokemonCaptureDTO;
import albprojects.pokedex.exceptions.PokemonLimitIdException;
import albprojects.pokedex.exceptions.PokemonNotFoundException;
import albprojects.pokedex.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping( "/api" )
public class PokemonController
{
    @Autowired
    private PokemonService pokemonService;

    @GetMapping( "/pokemons" )
    public Page<PokemonBriefDTO> getAllPokemons( Pageable pageable )
    {
        return pokemonService.getAllPokemons( pageable );
    }

    @GetMapping( "/pokemons/{pokedexId}" )
    // @PathVariable is used to extract the pokedexId from the URL and pass it as a parameter to the method
    public PokemonCompleteDTO getPokemonById( @PathVariable Integer pokedexId )
    {
        if ( pokedexId < 1 || pokedexId > 151 ) {
            // ReponseStatusException is thrown to indicate that the provided pokedexId is invalid, with a message specifying that the ID must be between 1 and 151
            throw new PokemonLimitIdException( "ID must be between 1 and 151" );
        }
        if ( !pokemonService.existsByPokedexId( pokedexId ) ) {
            throw new PokemonNotFoundException( "Pokemon not found with id: " + pokedexId );
        }
        return pokemonService.getPokemonById( pokedexId );
    }

    @PostMapping( "/pokemons" )
    // @RequestBody is used to bind the incoming JSON payload to the PokemonCompleteDTO object, allowing us to receive
    // the details of the new Pokemon to be registered in the system
    // ResponseEntity is used to return a response with a status code and a message indicating that the Pokemon was registered successfully
    public ResponseEntity<String> registerPokemon( @RequestBody PokemonCompleteDTO pokemonCompleteDTO )
    {
        pokemonService.registerPokemon( pokemonCompleteDTO );
        return ResponseEntity.ok( "Pokemon registered successfully" );
    }

    @PostMapping( "/pokemons/capture" )
    // @RequestBody is used to bind the incoming JSON payload to the PokemonCaptureDTO object, allowing us to receive
    // the pokedexId and name of the Pokemon to capture
    public PokemonCompleteDTO capturePokemon( @RequestBody PokemonCaptureDTO pokemonCaptureDTO )
    {
        return pokemonService.capturePokemon( pokemonCaptureDTO.pokedexId(), pokemonCaptureDTO.name() );
    }

    @DeleteMapping ( "/pokemons/{pokedexId}" )
    // @PathVariable is used to extract the pokedexId from the URL and pass
    // it as a parameter to the method, allowing us to identify which Pokemon to release from the Pokedex
    public ResponseEntity<String> releasePokemon( @PathVariable Integer pokedexId )
    {
        pokemonService.releasePokemon( pokedexId );
        return ResponseEntity.ok( "Pokemon N. " + pokedexId + " has been released successfully" );
    }

    @DeleteMapping ( "/pokemons" )
    // @RequestBody is used to bind the incoming JSON payload to a list of pokedexIds, allowing us to receive the IDs of the Pokemons to be released from the Pokedex
    public ResponseEntity<String> releaseAllPokemons ( )
    {
        pokemonService.releaseAllPokemons();
        return ResponseEntity.ok("All pokemons have been released successfully");
    }
}
