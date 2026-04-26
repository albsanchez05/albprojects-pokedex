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
        path: "pokedex",
        canActivate: [authGuard],
        loadComponent: () => import("./features/pokedex-home/pokedex-home").then( m => m.PokedexHome )
    },
    {
        path: "",
        pathMatch: "full",
        redirectTo: "pokedex"
    }
];
