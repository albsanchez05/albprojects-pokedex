import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

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

@Injectable( {
  providedIn: 'root'
} )
export class AuthService {
  // Base URL for the authentication API endpoints
  private readonly API_URL = 'http://localhost:8082/api/auth';
  // Key used to store the JWT in the browser's local storage
  private readonly TOKEN_KEY = 'auth_token';

  // Inject the HttpClient and PLATFORM_ID to perform HTTP requests and safely check SSR
  constructor( 
    private http: HttpClient,
    @Inject( PLATFORM_ID ) private platformId: Object
  ) {}

  // Authenticate a user and save the resulting token
  public login( request: LoginRequest ): Observable<AuthResponse> {
    return this.http.post<AuthResponse>( `${ this.API_URL }/login`, request ).pipe(
      tap( ( response: AuthResponse ) => this.setToken( response.token ) )
    );
  }

  // Register a new user and save the resulting token
  public register( request: RegisterRequest ): Observable<AuthResponse> {
    return this.http.post<AuthResponse>( `${ this.API_URL }/register`, request ).pipe(
      tap( ( response: AuthResponse ) => this.setToken( response.token ) )
    );
  }

  // Clear the authentication session by removing the token
  public logout(): void {
    if ( isPlatformBrowser( this.platformId ) ) {
      localStorage.removeItem( this.TOKEN_KEY );
    }
  }

  // Persist the token to local storage
  public setToken( token: string ): void {
    if ( isPlatformBrowser( this.platformId ) ) {
      localStorage.setItem( this.TOKEN_KEY, token );
    }
  }

  // Retrieve the stored token
  public getToken(): string | null {
    if ( isPlatformBrowser( this.platformId ) ) {
      return localStorage.getItem( this.TOKEN_KEY );
    }
    return null;
  }

  // Check if a token currently exists to determine authentication state
  public isLoggedIn(): boolean {
    return this.getToken() !== null;
  }
}