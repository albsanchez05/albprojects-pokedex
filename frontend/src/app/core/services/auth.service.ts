import { Injectable, Inject, PLATFORM_ID } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { HttpClient } from "@angular/common/http";
import { Observable, tap } from "rxjs";

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
  private readonly TOKEN_KEY = "auth_token";

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
    if ( isPlatformBrowser( this.platformId ) ) {
      localStorage.removeItem( this.TOKEN_KEY );
    }
  }

  public setToken( token: string ): void {
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
}