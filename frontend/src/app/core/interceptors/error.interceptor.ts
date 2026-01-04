import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Une erreur est survenue';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = `Erreur: ${error.error.message}`;
      } else {
        // Server-side error
        switch (error.status) {
          case 401:
            errorMessage = error.error?.message || 'Non autorisé. Veuillez vous reconnecter.';
            // Clear auth and redirect to login
            localStorage.clear();
            router.navigate(['/auth/login']);
            break;
          case 403:
            errorMessage = error.error?.message || 'Accès interdit';
            break;
          case 404:
            errorMessage = error.error?.message || 'Ressource non trouvée';
            break;
          case 409:
            errorMessage = error.error?.message || 'Conflit (doublon)';
            break;
          case 500:
            errorMessage = error.error?.message || 'Erreur serveur. Veuillez réessayer plus tard.';
            break;
          default:
            errorMessage = error.error?.message || `Erreur ${error.status}: ${error.statusText}`;
        }
      }

      // Show error message in snackbar
      snackBar.open(errorMessage, 'Fermer', {
        duration: 5000,
        horizontalPosition: 'end',
        verticalPosition: 'top',
        panelClass: ['error-snackbar']
      });

      return throwError(() => error);
    })
  );
};

