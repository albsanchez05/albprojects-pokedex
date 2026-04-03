package albprojects.pokedex.controller;

import albprojects.pokedex.dto.PokemonBriefDTO;
import albprojects.pokedex.dto.PokemonCaptureDTO;
import albprojects.pokedex.dto.PokemonCompleteDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag( name = "Pokemon API", description = "API for managing Pokemons in the Pokedex" )
public interface PokemonControllerAPI {

    @Operation( summary = "Get all Pokemons", description = "Retrieve a paginated list of all Pokemons in the Pokedex" )
    @ApiResponse( responseCode = "200", description = "Successfully retrieved the list of Pokemons" )
    Page<PokemonBriefDTO> getAllPokemons( @ParameterObject Pageable pageable );

    @Operation( summary = "Get Pokemon by Pokedex ID", description = "Retrieve detailed information about a specific Pokemon using its Pokedex ID" )
    @ApiResponse( responseCode = "200", description = "Successfully retrieved the Pokemon details" )
    @ApiResponse( responseCode = "404", description = "Pokemon not found with the specified Pokedex ID" )
    @Parameter( name = "pokedexId", description = "The Pokedex ID of the Pokemon to retrieve", example = "1" )
    PokemonCompleteDTO getPokemonById( @PathVariable Integer pokedexId );

    @Operation( summary = "Capture a Pokemon", description = "Mark a specific Pokemon as captured in the Pokedex using its Pokedex ID" )
    @ApiResponse( responseCode = "200", description = "Successfully updated the captured status of the Pokemon" )
    @ApiResponse( responseCode = "404", description = "Pokemon not found with the specified Pokedex ID" )
    @Parameter( name = "id", description = "The Pokedex ID of the Pokemon to capture", example = "1" )
    PokemonCompleteDTO capturePokemon( @RequestBody PokemonCaptureDTO pokemonCaptureDTO );

    @Operation( summary = "Register a new Pokemon", description = "Add a new Pokemon to the Pokedex with its complete details. 'captured' status will default to false." )
    @ApiResponse( responseCode = "200", description = "Successfully registered the new Pokemon" )
    @ApiResponse( responseCode = "400", description = "Invalid input data or Pokemon with the same ID or name already exists" )
    void registerPokemon( @RequestBody PokemonCompleteDTO pokemonCompleteDTO );

    @Operation( summary = "Unregister a Pokemon", description = "Permanently removes a Pokemon from the Pokedex system based on its Pokedex ID." )
    @ApiResponse( responseCode = "204", description = "Successfully unregistered and removed the Pokemon, returning no content" )
    @ApiResponse( responseCode = "404", description = "Pokemon not found with the specified Pokedex ID" )
    @Parameter( name = "pokedexId", description = "The Pokedex ID of the Pokemon to unregister", example = "1" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    void releasePokemon( @PathVariable Integer pokedexId );
}
