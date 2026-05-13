// File: frontend/src/app/core/services/pokemon.service.ts
// Purpose: Implements frontend service logic and backend API communication.
import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { PokemonPageModel } from "../models/pokemon-page.model";
import { PokemonDetailModel } from "../models/pokemon-detail.model";
import { AuthService } from "./auth.service";

@Injectable( {
  providedIn: "root"
} )
export class PokemonService {
  private readonly apiUrl = "/api/pokemons";

  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService
  ) {}

  private getAuthHeaders(): HttpHeaders | undefined {
    const token = this.authService.getToken();
    return token
      ? new HttpHeaders( { Authorization: `Bearer ${ token }` } )
      : undefined;
  }

  public getPokemons( page: number, size: number ): Observable<PokemonPageModel> {
    const headers = this.getAuthHeaders();

    return this.http.get<PokemonPageModel>( `${ this.apiUrl }?page=${ page }&size=${ size }`, { headers } );
  }

  public getPokemonById( pokedexId: number ): Observable<PokemonDetailModel> {
    const headers = this.getAuthHeaders();
    return this.http.get<PokemonDetailModel>( `${ this.apiUrl }/${ pokedexId }`, { headers } );
  }

  public updateCaptureStatus( pokedexId: number, captured: boolean ): Observable<PokemonDetailModel> {
    const headers = this.getAuthHeaders();
    const body = {
      pokedexId,
      captured
    };

    return this.http.post<PokemonDetailModel>( `${ this.apiUrl }/${ pokedexId }`, body, { headers } );
  }

  public registerPokemon( pokemon: PokemonDetailModel ): Observable<PokemonDetailModel> {
    const headers = this.getAuthHeaders();
    return this.http.post<PokemonDetailModel>( this.apiUrl, pokemon, { headers } );
  }

  public deletePokemon( pokedexId: number ): Observable<void> {
    const headers = this.getAuthHeaders();
    return this.http.delete<void>( `${ this.apiUrl }/${ pokedexId }`, { headers } );
  }

  public searchExternalPokemon( idOrName: string ): Observable<PokemonDetailModel> {
    const headers = this.getAuthHeaders();
    return this.http.get<PokemonDetailModel>( `${ this.apiUrl }/external/${ encodeURIComponent( idOrName ) }`, { headers } );
  }

  public importExternalPokemon( idOrName: string ): Observable<PokemonDetailModel> {
    const headers = this.getAuthHeaders();
    return this.http.post<PokemonDetailModel>( `${ this.apiUrl }/external/${ encodeURIComponent( idOrName ) }/import`, {}, { headers } );
  }
}
