// File: frontend/src/app/app.routes.server.ts
// Purpose: Defines frontend route configuration for client and server rendering.
import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: '**',
    renderMode: RenderMode.Server
  }
];
