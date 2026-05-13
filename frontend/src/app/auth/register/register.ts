// File: frontend/src/app/auth/register/register.ts
// Purpose: Defines frontend component logic for UI behavior and interactions.
import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { HttpErrorResponse } from "@angular/common/http";
import { AuthService, RegisterRequest } from "../../core/services/auth.service";

@Component( {
  selector: "app-register",
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: "./register.html",
  styleUrl: "./register.css"
} )
export class Register {
  public errorMessage = "";
  public isSubmitting = false;
  public readonly form;

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {
    this.form = this.fb.group( {
      username: ["", [Validators.required, Validators.minLength( 3 )]],
      email: ["", [Validators.required, Validators.email, Validators.pattern( /^[^\s@]+@[^\s@]+\.[A-Za-z]{2,}$/ )]],
      password: ["", [Validators.required, Validators.minLength( 8 )]]
    } );
  }

  public onSubmit(): void {
    if ( this.form.invalid ) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = "";
    this.isSubmitting = true;
    this.clearConflictErrors();

    const payload = this.form.getRawValue() as RegisterRequest;

    this.authService.register( payload ).subscribe( {
      next: () => {
        this.isSubmitting = false;
        this.router.navigateByUrl( "/login" );
      },
      error: ( err: HttpErrorResponse ) => {
        this.isSubmitting = false;
        const backendMessage = this.extractBackendMessage( err ).toLowerCase();

        if ( err.status === 409 ) {
          if ( backendMessage.includes( "username already taken" ) ) {
            this.form.get( "username" )?.setErrors( { ...( this.form.get( "username" )?.errors || {} ), conflict: true } );
            this.form.get( "username" )?.markAsTouched();
            this.errorMessage = "This username already exists.";
            return;
          }

          if ( backendMessage.includes( "email already registered" ) ) {
            this.form.get( "email" )?.setErrors( { ...( this.form.get( "email" )?.errors || {} ), conflict: true } );
            this.form.get( "email" )?.markAsTouched();
            this.errorMessage = "This email is already registered.";
            return;
          }

          this.errorMessage = "A user with this username or email already exists.";
          return;
        }
        this.errorMessage = this.extractBackendMessage( err ) || "Registration failed. Please review the data.";
      }
    } );
  }

  private extractBackendMessage( err: HttpErrorResponse ): string {
    const payload = err?.error;

    if ( typeof payload === "string" ) {
      try {
        const parsed = JSON.parse( payload );
        return ( parsed?.error || parsed?.message || payload ).toString();
      } catch {
        return payload;
      }
    }

    if ( payload?.error ) {
      return payload.error.toString();
    }

    if ( payload?.message ) {
      return payload.message.toString();
    }

    return "";
  }

  private clearConflictErrors(): void {
    const usernameControl = this.form.get( "username" );
    const emailControl = this.form.get( "email" );

    if ( usernameControl?.errors?.["conflict"] ) {
      const { conflict, ...rest } = usernameControl.errors;
      usernameControl.setErrors( Object.keys( rest ).length ? rest : null );
    }

    if ( emailControl?.errors?.["conflict"] ) {
      const { conflict, ...rest } = emailControl.errors;
      emailControl.setErrors( Object.keys( rest ).length ? rest : null );
    }
  }
}
