import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService); //use inject() instead of constructor
  const credentials = authService.getCredentials();

  // Check if user is logged in AND if the request is NOT for the public auth endpoints
  if (credentials && !req.url.includes('/api/auth')) {
    const { username, password } = credentials;

    // Create the Base64 encoded string for Basic Auth
    const basicAuthHeader = 'Basic ' + btoa(username + ':' + password);

    // Clone the request and add the Authorization header
    const authReq = req.clone({
      headers: req.headers.set('Authorization', basicAuthHeader)
    });

    return next(authReq);
  }

  // If no credentials or it's a public endpoint, pass the original request
  return next(req);
};
