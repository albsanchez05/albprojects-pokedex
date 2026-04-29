import { Routes } from "@angular/router";
import { authGuard } from "./core/guards/auth-guard";

export const routes: Routes = [
    {
        path: "login",
        loadComponent: () => import("./auth/login/login").then( m => m.Login )
    },
    {
        path: "register",
        loadComponent: () => import("./auth/register/register").then( m => m.Register )
    },
    {
        path: "pokedex/:id",
        canActivate: [authGuard],
        loadComponent: () => import("./features/pokemon/pokemon-detail/pokemon-detail").then( m => m.PokemonDetail )
    },
    {
        path: "pokedex",
        canActivate: [authGuard],
        loadComponent: () => import("./features/pokemon/pokedex-grid/pokedex-grid").then( m => m.PokedexGrid )
    },
    {
        path: "",
        pathMatch: "full",
        redirectTo: "pokedex"
    }
];
