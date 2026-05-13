// File: frontend/src/app/core/models/pokemon-detail.model.ts
// Purpose: Defines typed frontend models used by components and services.
export interface PokemonDetailModel {
  pokemonId: number;
  name: string;
  type1: string;
  type2: string | null;
  hp: number;
  attack: number;
  defense: number;
  spAttack: number;
  spDefense: number;
  speed: number;
  image: string;
  captured: boolean;
}
