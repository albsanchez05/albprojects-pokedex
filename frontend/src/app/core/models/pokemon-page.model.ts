// File: frontend/src/app/core/models/pokemon-page.model.ts
// Purpose: Defines typed frontend models used by components and services.
import { PokemonBriefModel } from "./pokemon-brief.model";

export interface PokemonPageModel {
  content: PokemonBriefModel[];
  number: number;
  size: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
