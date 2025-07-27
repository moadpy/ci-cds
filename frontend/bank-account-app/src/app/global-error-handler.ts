import { ErrorHandler, Injectable } from '@angular/core';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  handleError(error: any): void {
    // Check for AbortError in various forms it might appear
    const isAbortError =
      error?.name === 'AbortError' ||
      error?.rejection?.name === 'AbortError' ||
      error?.message?.includes('AbortError') ||
      error?.error?.name === 'AbortError' ||
      (error && error.toString().includes('AbortError'));

    if (isAbortError) {
      // Silently ignore AbortError - these occur during navigation and are not actual errors
      return;
    }

    // For all other errors, log them to console
    console.error('Global error caught:', error);
  }
}
