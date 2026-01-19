import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    tap({
      error: (err) => {
        console.error('API Error:', err);

        if (err.status === 0) {
          alert('Cannot reach server. Check your connection.');
        }

        if (err.status === 404) {
          alert('Resource not found.');
        }

        if (err.status === 500) {
          alert('Internal server error.');
        }
      }
    })
  );
};