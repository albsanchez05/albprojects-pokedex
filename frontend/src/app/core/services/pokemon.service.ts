import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { PokemonPageModel } from "../models/pokemon-page.model";
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

  public getPokemons( page: number, size: number ): Observable<PokemonPageModel> {
    const token = this.authService.getToken();
    const headers = token
      ? new HttpHeaders( { Authorization: `Bearer ${ token }` } )
      : undefined;

    return this.http.get<PokemonPageModel>( `${ this.apiUrl }?page=${ page }&size=${ size }`, { headers } );
  }
}