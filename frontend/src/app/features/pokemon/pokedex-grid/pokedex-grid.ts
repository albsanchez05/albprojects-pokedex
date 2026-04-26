import { ChangeDetectorRef, Component, Inject, OnInit, PLATFORM_ID } from "@angular/core";
import { CommonModule } from "@angular/common";
import { isPlatformBrowser } from "@angular/common";
import { PokemonService } from "../../../core/services/pokemon.service";
import { PokemonPageModel } from "../../../core/models/pokemon-page.model";
import { PokemonCard } from "../pokemon-card/pokemon-card";
import { Navbar } from "../../../core/components/navbar/navbar";
import { finalize, timeout } from "rxjs";

@Component( {
  selector: "app-pokedex-grid",
  imports: [CommonModule, PokemonCard, Navbar],
  templateUrl: "./pokedex-grid.html",
  styleUrl: "./pokedex-grid.css"
} )
export class PokedexGrid implements OnInit {
  public pokemonPage: PokemonPageModel | null = null;
  public isLoading = true;
  public errorMessage = "";
  public currentPage = 0;
  public readonly pageSize = 5;

  constructor(
    private readonly pokemonService: PokemonService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    @Inject( PLATFORM_ID ) private readonly platformId: object
  ) {}

  public ngOnInit(): void {
    if ( !isPlatformBrowser( this.platformId ) ) {
      return;
    }

    this.loadPage( 0 );
  }

  public loadPage( page: number ): void {
    this.isLoading = true;
    this.errorMessage = "";

    this.pokemonService.getPokemons( page, this.pageSize )
      .pipe(
        timeout( 10000 ),
        finalize( () => {
          this.isLoading = false;
          this.changeDetectorRef.detectChanges();
        } )
      )
      .subscribe( {
      next: ( response ) => {
        if ( !response || !Array.isArray( response.content ) ) {
          this.errorMessage = "Unexpected response while loading Pokemons.";
          return;
        }

        this.pokemonPage = response;
        this.currentPage = response.number;
        this.changeDetectorRef.detectChanges();
      },
      error: ( err ) => {
        if ( err?.status === 403 ) {
          this.errorMessage = "Access denied. Please login again.";
          this.changeDetectorRef.detectChanges();
          return;
        }

        if ( err?.name === "TimeoutError" ) {
          this.errorMessage = "The request timed out. Please try again.";
          this.changeDetectorRef.detectChanges();
          return;
        }

        this.errorMessage = "Could not load Pokemons.";
        this.changeDetectorRef.detectChanges();
      }
    } );
  }

  public goToNextPage(): void {
    if ( this.pokemonPage && !this.pokemonPage.last ) {
      this.loadPage( this.currentPage + 1 );
    }
  }

  public goToPreviousPage(): void {
    if ( this.pokemonPage && !this.pokemonPage.first ) {
      this.loadPage( this.currentPage - 1 );
    }
  }
}