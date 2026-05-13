// File: frontend/src/app/features/pokemon/pokemon-card/pokemon-card.ts
// Purpose: Defines frontend component logic for UI behavior and interactions.
import { Component, Input } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";
import { PokemonBriefModel } from "../../../core/models/pokemon-brief.model";

@Component( {
  selector: "app-pokemon-card",
  imports: [CommonModule, RouterLink],
  templateUrl: "./pokemon-card.html",
  styleUrl: "./pokemon-card.css"
} )
export class PokemonCard {
  @Input( { required: true } ) public pokemon!: PokemonBriefModel;
}