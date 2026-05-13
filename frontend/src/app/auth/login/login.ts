// File: frontend/src/app/auth/login/login.ts
// Purpose: Defines frontend component logic for UI behavior and interactions.
import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { AuthService, LoginRequest } from "../../core/services/auth.service";

@Component( {
  selector: "app-login",
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: "./login.html",
  styleUrl: "./login.css"
} )
export class Login {
  public errorMessage = "";
  public isSubmitting = false;
  public readonly form;

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {
    this.form = this.fb.group( {
      username: ["", [Validators.required]],
      password: ["", [Validators.required]]
    } );
  }

  public onSubmit(): void {
    if ( this.form.invalid ) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = "";
    this.isSubmitting = true;

    const payload = this.form.getRawValue() as LoginRequest;

    this.authService.login( payload ).subscribe( {
      next: () => {
        this.isSubmitting = false;
        this.router.navigateByUrl( "/pokedex" );
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMessage = "Invalid username or password.";
      }
    } );
  }
}