import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: '',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'accounts',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'accounts/new',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'accounts/edit/:id',
    renderMode: RenderMode.Server
  },
  {
    path: '**',
    renderMode: RenderMode.Server
  }
];
