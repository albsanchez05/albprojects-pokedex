import { ChangeDetectorRef, Component, Inject, OnInit, PLATFORM_ID } from "@angular/core";
import { CommonModule, isPlatformBrowser } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { PokemonService } from "../../../core/services/pokemon.service";
import { AuthService } from "../../../core/services/auth.service";
import { PokemonPageModel } from "../../../core/models/pokemon-page.model";
import { PokemonDetailModel } from "../../../core/models/pokemon-detail.model";
import { PokemonCard } from "../pokemon-card/pokemon-card";
import { Navbar } from "../../../core/components/navbar/navbar";
import { finalize, timeout } from "rxjs";

@Component( {
  selector: "app-pokedex-grid",
  imports: [CommonModule, FormsModule, PokemonCard, Navbar],
  templateUrl: "./pokedex-grid.html",
  styleUrl: "./pokedex-grid.css"
} )
export class PokedexGrid implements OnInit {
  public pokemonPage: PokemonPageModel | null = null;
  public isLoading = true;
  public isAdmin = false;
  public errorMessage = "";
  public registerErrorMessage = "";
  public registerSuccessMessage = "";
  public showRegisterForm = false;
  public isRegistering = false;
  public currentPage = 0;
  public readonly pageSize = 5;
  public newPokemon: PokemonDetailModel = this.createEmptyPokemon();
  public externalSearchQuery = "";
  public isSearchingExternal = false;
  public isImportingExternal = false;
  public externalSearchErrorMessage = "";
  public externalSearchSuccessMessage = "";
  public externalPreviewLoaded = false;

  constructor(
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

  public toggleRegisterForm(): void {
    this.showRegisterForm = !this.showRegisterForm;
    this.registerErrorMessage = "";
    this.registerSuccessMessage = "";
    this.externalSearchQuery = "";
    this.externalSearchErrorMessage = "";
    this.externalSearchSuccessMessage = "";
    this.externalPreviewLoaded = false;

    if ( this.showRegisterForm ) {
      this.newPokemon = this.createEmptyPokemon();
    }
  }

  public registerPokemon(): void {
    this.externalSearchErrorMessage = "";
    this.externalSearchSuccessMessage = "";
    if ( !this.isAdmin || this.isRegistering ) {
      return;
    }

    if ( !this.isValidPokemonInput() ) {
      this.registerErrorMessage = "Please complete all required fields with valid values.";
      return;
    }

    this.isRegistering = true;
    this.registerErrorMessage = "";
    this.registerSuccessMessage = "";

    this.pokemonService.registerPokemon( {
      ...this.newPokemon,
      captured: false
    } )
      .pipe( finalize( () => {
        this.isRegistering = false;
        this.changeDetectorRef.detectChanges();
      } ) )
      .subscribe( {
        next: () => {
          this.registerSuccessMessage = "Pokemon registered successfully.";
          this.showRegisterForm = false;
          this.newPokemon = this.createEmptyPokemon();
          this.loadPage( 0 );
        },
        error: ( err ) => {
          if ( err?.status === 409 || err?.status === 400 ) {
            this.registerErrorMessage = "Pokemon could not be registered. Check ID and name uniqueness.";
            return;
          }

          if ( err?.status === 403 ) {
            this.registerErrorMessage = "Access denied. Admin role is required.";
            return;
          }

          this.registerErrorMessage = "Could not register Pokemon.";
        }
      } );
  }

  private isValidPokemonInput(): boolean {
    const p = this.newPokemon;
    return p.pokemonId > 0
      && p.name.trim().length > 0
      && p.type1.trim().length > 0
      && p.image.trim().length > 0
      && p.hp > 0
      && p.attack > 0
      && p.defense > 0
      && p.spAttack > 0
      && p.spDefense > 0
      && p.speed > 0;
  }

  private createEmptyPokemon(): PokemonDetailModel {
    return {
      pokemonId: 0,
      name: "",
      type1: "",
      type2: null,
      hp: 1,
      attack: 1,
      defense: 1,
      spAttack: 1,
      spDefense: 1,
      speed: 1,
      image: "",
      captured: false
    };
  }

 public searchExternalPokemon(): void {
  if ( !this.isAdmin || this.isSearchingExternal ) {
    return;
  }

  const query = this.externalSearchQuery.trim();
  if ( query.length === 0 ) {
    this.externalSearchErrorMessage = "Please enter a Pokemon name or ID.";
    this.externalSearchSuccessMessage = "";
    return;
  }

  this.isSearchingExternal = true;
  this.externalSearchErrorMessage = "";
  this.externalSearchSuccessMessage = "";

  this.pokemonService.searchExternalPokemon( query )
    .pipe( finalize( () => {
      this.isSearchingExternal = false;
      this.changeDetectorRef.detectChanges();
    } ) )
    .subscribe( {
      next: ( pokemon ) => {
        this.newPokemon = {
          ...pokemon,
          captured: false
        };
        this.externalPreviewLoaded = true;
        this.externalSearchSuccessMessage = "External Pokemon data loaded. Review and import or save.";
      },
      error: ( err ) => {
        this.externalPreviewLoaded = false;

        if ( err?.status === 404 ) {
          this.externalSearchErrorMessage = "Pokemon not found in external source.";
          return;
        }

        if ( err?.status === 403 ) {
          this.externalSearchErrorMessage = "Access denied. Admin role is required.";
          return;
        }

        if ( err?.status === 504 ) {
          this.externalSearchErrorMessage = "External service timeout. Please try again.";
          return;
        }

        this.externalSearchErrorMessage = "Could not fetch external Pokemon data.";
      }
    } );
  }

  public importExternalPokemon(): void {
    if ( !this.isAdmin || this.isImportingExternal ) {
      return;
    }

    const query = this.externalSearchQuery.trim();
    if ( query.length === 0 ) {
      this.externalSearchErrorMessage = "Please enter a Pokemon name or ID.";
      this.externalSearchSuccessMessage = "";
      return;
    }

    this.isImportingExternal = true;
    this.externalSearchErrorMessage = "";
    this.externalSearchSuccessMessage = "";
    this.registerErrorMessage = "";
    this.registerSuccessMessage = "";

    this.pokemonService.importExternalPokemon( query )
      .pipe( finalize( () => {
        this.isImportingExternal = false;
        this.changeDetectorRef.detectChanges();
      } ) )
      .subscribe( {
        next: () => {
          this.externalPreviewLoaded = false;
          this.externalSearchSuccessMessage = "Pokemon imported successfully from external source.";
          this.registerSuccessMessage = "Pokemon imported and registered successfully.";
          this.newPokemon = this.createEmptyPokemon();
          this.externalSearchQuery = "";
          this.loadPage( 0 );
        },
        error: ( err ) => {
          if ( err?.status === 400 ) {
            this.externalSearchErrorMessage = "Pokemon already exists in local Pokedex.";
            return;
          }

          if ( err?.status === 404 ) {
            this.externalSearchErrorMessage = "Pokemon not found in external source.";
            return;
          }

          if ( err?.status === 403 ) {
            this.externalSearchErrorMessage = "Access denied. Admin role is required.";
            return;
          }

          if ( err?.status === 504 ) {
            this.externalSearchErrorMessage = "External service timeout. Please try again.";
            return;
          }

          this.externalSearchErrorMessage = "Could not import Pokemon from external source.";
        }
      } );
  }
}
