import { PokemonBriefModel } from "./pokemon-brief.model";

export interface PokemonPageModel {
  content: PokemonBriefModel[];
  number: number;
  size: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
