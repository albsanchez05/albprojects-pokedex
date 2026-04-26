import { Component } from "@angular/core";
import { Navbar } from "../../core/components/navbar/navbar";

@Component( {
  selector: "app-pokedex-home",
  imports: [Navbar],
  templateUrl: "./pokedex-home.html",
  styleUrl: "./pokedex-home.css"
} )
export class PokedexHome {}