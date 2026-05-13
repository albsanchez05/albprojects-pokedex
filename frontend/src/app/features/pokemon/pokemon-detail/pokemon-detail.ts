// File: frontend/src/app/features/pokemon/pokemon-detail/pokemon-detail.ts
// Purpose: Defines frontend component logic for UI behavior and interactions.
import { CommonModule, isPlatformBrowser } from "@angular/common";
import { ChangeDetectorRef, Component, Inject, OnInit, PLATFORM_ID } from "@angular/core";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { finalize, timeout } from "rxjs";
import { PokemonDetailModel } from "../../../core/models/pokemon-detail.model";
import { Navbar } from "../../../core/components/navbar/navbar";
import { PokemonService } from "../../../core/services/pokemon.service";
import { AuthService } from "../../../core/services/auth.service";

@Component( {
  selector: "app-pokemon-detail",
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: "./pokemon-detail.html",
  styleUrls: ["./pokemon-detail.css"]
} )
export class PokemonDetail implements OnInit {
  public pokemon: PokemonDetailModel | null = null;
  public isLoading = true;
  public isAdmin = false;
  public isSaving = false;
  public errorMessage = "";

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly pokemonService: PokemonService,
    private readonly authService: AuthService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    @Inject( PLATFORM_ID ) private readonly platformId: object
  ) {}

  public ngOnInit(): void {
    if ( !isPlatformBrowser( this.platformId ) ) {
      return;
    }

    this.authService.resolveAdminStatus().subscribe( {
      next: ( isAdminUser ) => {
        this.isAdmin = isAdminUser;
        this.changeDetectorRef.detectChanges();
      }
    } );

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

  public deletePokemon(): void {
    if ( !this.pokemon || !this.isAdmin || this.isSaving ) {
      return;
    }

    if ( !confirm( `Are you sure you want to delete ${ this.pokemon.name }?` ) ) {
      return;
    }

    this.isSaving = true;
    this.errorMessage = "";

    this.pokemonService.deletePokemon( this.pokemon.pokemonId )
      .pipe( finalize( () => {
        this.isSaving = false;
        this.changeDetectorRef.detectChanges();
      } ) )
      .subscribe( {
        next: () => {
          // Redirect back to grid after deletion
          this.router.navigate( ["/pokedex"] );
        },
        error: () => {
          this.errorMessage = "Could not delete Pokemon.";
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
