import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
// Import the main application component and configuration
bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
