// File: frontend/src/app/core/services/auth.service.ts
// Purpose: Implements frontend service logic and backend API communication.
import { Injectable, Inject, PLATFORM_ID } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { catchError, map, Observable, of, tap } from "rxjs";

// Interface representing the authentication response from the backend
export interface AuthResponse {
  token: string;
}

// Interface for the login request payload
export interface LoginRequest {
  username: string;
  password: string;
}

// Interface for the register request payload
export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

@Injectable({
  providedIn: "root"
})
export class AuthService {
  // Use a relative path so the same code works locally and in Docker
  private readonly API_URL = "/api/auth";
  private readonly POKEMON_API_URL = "/api/pokemons";
  private readonly TOKEN_KEY = "auth_token";
  private adminStatus: boolean | null = null;

  constructor(
    private readonly http: HttpClient,
    @Inject( PLATFORM_ID ) private readonly platformId: object
  ) {}

  public login( request: LoginRequest ): Observable<AuthResponse> {
    return this.http.post<AuthResponse>( `${ this.API_URL }/login`, request ).pipe(
      tap( ( response: AuthResponse ) => this.setToken( response.token ) )
    );
  }

  public register( request: RegisterRequest ): Observable<AuthResponse> {
    return this.http.post<AuthResponse>( `${ this.API_URL }/register`, request ).pipe(
      tap( ( response: AuthResponse ) => this.setToken( response.token ) )
    );
  }

  public logout(): void {
    this.adminStatus = null;

    if ( isPlatformBrowser( this.platformId ) ) {
      localStorage.removeItem( this.TOKEN_KEY );
    }
  }

  public setToken( token: string ): void {
    this.adminStatus = null;

    if ( isPlatformBrowser( this.platformId ) ) {
      localStorage.setItem( this.TOKEN_KEY, token );
    }
  }

  public getToken(): string | null {
    if ( isPlatformBrowser( this.platformId ) ) {
      return localStorage.getItem( this.TOKEN_KEY );
    }
    return null;
  }

  public isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  public isAdmin(): boolean {
    if ( this.adminStatus !== null ) {
      return this.adminStatus;
    }

    if ( !isPlatformBrowser( this.platformId ) ) {
      return false;
    }

    const token = this.getToken();
    if ( !token ) {
      return false;
    }

    try {
      // Decode JWT payload without verification (frontend convenience only)
      const parts = token.split( "." );
      if ( parts.length !== 3 ) {
        return false;
      }

      const normalizedPayload = parts[1]
        .replace( /-/g, "+" )
        .replace( /_/g, "/" );
      const decoded = JSON.parse( atob( normalizedPayload ) );

      const candidateClaims: unknown[] = [
        decoded.role,
        decoded.roles,
        decoded.authorities,
        decoded.authority,
        decoded.scope,
        decoded.scp
      ];

      const hasAnyRoleClaim = candidateClaims.some( claim => claim !== undefined && claim !== null );

      const hasAdminRole = candidateClaims.some( claim => {
        if ( typeof claim === "string" ) {
          return claim.includes( "ADMIN" );
        }

        if ( Array.isArray( claim ) ) {
          return claim.some( entry => String( entry ).includes( "ADMIN" ) );
        }

        return false;
      } );

      // Only cache negative result if role claims actually exist in JWT.
      // If claims are missing, let resolveAdminStatus() probe API permissions.
      this.adminStatus = hasAdminRole ? true : ( hasAnyRoleClaim ? false : null );
      return hasAdminRole;
    } catch ( error ) {
      // If JWT parsing fails, assume not admin
      return false;
    }
  }

  public resolveAdminStatus(): Observable<boolean> {
    if ( this.adminStatus !== null ) {
      return of( this.adminStatus );
    }

    if ( !isPlatformBrowser( this.platformId ) ) {
      return of( false );
    }

    const token = this.getToken();
    if ( !token ) {
      this.adminStatus = false;
      return of( false );
    }

    const parsedRole = this.isAdmin();
    if ( this.adminStatus !== null ) {
      return of( parsedRole );
    }

    const headers = new HttpHeaders( { Authorization: `Bearer ${ token }` } );
    const invalidPayload = {
      pokemonId: -1,
      name: "",
      type1: "",
      type2: null,
      hp: 0,
      attack: 0,
      defense: 0,
      spAttack: 0,
      spDefense: 0,
      speed: 0,
      image: ""
    };

    return this.http.post( this.POKEMON_API_URL, invalidPayload, { headers, observe: "response" } )
      .pipe(
        map( () => {
          this.adminStatus = true;
          return true;
        } ),
        catchError( ( err: HttpErrorResponse ) => {
          const canAccessAdminEndpoint = err.status === 400 || err.status === 409;
          this.adminStatus = canAccessAdminEndpoint;
          return of( canAccessAdminEndpoint );
        } )
      );
  }
}