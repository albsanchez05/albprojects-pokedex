import { CommonModule, isPlatformBrowser } from "@angular/common";
import { ChangeDetectorRef, Component, Inject, OnInit, PLATFORM_ID } from "@angular/core";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { finalize, timeout } from "rxjs";
import { PokemonDetailModel } from "../../../core/models/pokemon-detail.model";
import { Navbar } from "../../../core/components/navbar/navbar";
import { PokemonService } from "../../../core/services/pokemon.service";

@Component( {
  selector: "app-pokemon-detail",
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: "./pokemon-detail.html",
  styleUrls: ["./pokemon-detail.css"]
} )
export class PokemonDetail implements OnInit {
  public pokemon: PokemonDetailModel | null = null;
  public isLoading = true;
  public isSaving = false;
  public errorMessage = "";

  constructor(
    private readonly route: ActivatedRoute,
    private readonly pokemonService: PokemonService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    @Inject( PLATFORM_ID ) private readonly platformId: object
  ) {}

  public ngOnInit(): void {
    if ( !isPlatformBrowser( this.platformId ) ) {
      return;
    }

    const idParam = this.route.snapshot.paramMap.get( "id" );
    const pokedexId = Number( idParam );

    if ( !idParam || Number.isNaN( pokedexId ) ) {
      this.isLoading = false;
      this.errorMessage = "Invalid Pokemon id.";
      this.changeDetectorRef.detectChanges();
      return;
    }

    this.loadPokemon( pokedexId );
  }

  public toggleCapture(): void {
    if ( !this.pokemon || this.isSaving ) {
      return;
    }

    this.isSaving = true;
    this.errorMessage = "";

    const nextCapturedState = !this.pokemon.captured;
    this.pokemonService.updateCaptureStatus( this.pokemon.pokemonId, nextCapturedState )
      .pipe( finalize( () => {
        this.isSaving = false;
        this.changeDetectorRef.detectChanges();
      } ) )
      .subscribe( {
        next: ( updatedPokemon ) => {
          this.pokemon = updatedPokemon;
          this.changeDetectorRef.detectChanges();
        },
        error: () => {
          this.errorMessage = "Could not update capture status.";
          this.changeDetectorRef.detectChanges();
        }
      } );
  }

  private loadPokemon( pokedexId: number ): void {
    this.isLoading = true;
    this.errorMessage = "";

    this.pokemonService.getPokemonById( pokedexId )
      .pipe(
        timeout( 10000 ),
        finalize( () => {
          this.isLoading = false;
          this.changeDetectorRef.detectChanges();
        } )
      )
      .subscribe( {
        next: ( pokemon ) => {
          this.pokemon = pokemon;
          this.changeDetectorRef.detectChanges();
        },
        error: ( err ) => {
          if ( err?.status === 403 ) {
            this.errorMessage = "Access denied. Please login again.";
            this.changeDetectorRef.detectChanges();
            return;
          }

          if ( err?.status === 404 ) {
            this.errorMessage = "Pokemon not found.";
            this.changeDetectorRef.detectChanges();
            return;
          }

          if ( err?.name === "TimeoutError" ) {
            this.errorMessage = "The request timed out. Please try again.";
            this.changeDetectorRef.detectChanges();
            return;
          }

          this.errorMessage = "Could not load Pokemon details.";
          this.changeDetectorRef.detectChanges();
        }
      } );
  }
}
