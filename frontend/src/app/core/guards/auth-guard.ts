// File: frontend/src/app/core/guards/auth-guard.ts
// Purpose: Defines route guards for authentication and authorization checks.
import { inject, PLATFORM_ID } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

export const authGuard: CanActivateFn = () => {
  const platformId = inject( PLATFORM_ID );
  const authService = inject( AuthService );
  const router = inject( Router );

  if ( !isPlatformBrowser( platformId ) ) {
    return true;
  }

  if ( authService.isLoggedIn() ) {
    return true;
  }

  return router.createUrlTree( ["/login"] );
};
