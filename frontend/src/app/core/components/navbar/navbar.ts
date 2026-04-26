import { Component } from "@angular/core";
import { Router, RouterLink } from "@angular/router";
import { AuthService } from "../../services/auth.service";

@Component( {
  selector: "app-navbar",
  imports: [RouterLink],
  templateUrl: "./navbar.html",
  styleUrl: "./navbar.css"
} )

export class Navbar {
  constructor(
  private readonly authService: AuthService,
  private readonly router: Router
  ) {}

  public logout(): void {
  this.authService.logout();
  this.router.navigateByUrl( "/login" );
  }
}
