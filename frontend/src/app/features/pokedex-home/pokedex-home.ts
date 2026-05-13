// File: frontend/src/app/features/pokedex-home/pokedex-home.ts
// Purpose: Defines frontend component logic for UI behavior and interactions.
import { Component } from "@angular/core";
import { Navbar } from "../../core/components/navbar/navbar";

@Component( {
  selector: "app-pokedex-home",
  imports: [Navbar],
  templateUrl: "./pokedex-home.html",
  styleUrl: "./pokedex-home.css"
} )
export class PokedexHome {}