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
