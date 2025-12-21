import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthRequest } from '../models/IAuth';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl + '/auth';

  constructor(private http: HttpClient) { }

  register(request: AuthRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/register`, request, {
      responseType: 'text'
    });
  }

  login(request: AuthRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/login`, request, {
      responseType: 'text'
    }).pipe(
      tap(() => {
        sessionStorage.setItem('username', request.username);
        sessionStorage.setItem('password', request.password);
      })
    );
  }

  logout(): void {
    sessionStorage.clear();
  }

  isLoggedIn(): boolean {
    return !!sessionStorage.getItem('username');
  }

   getUsername(): string {
     return sessionStorage.getItem('username') || '';
  }

  getCredentials(): { username: string, password: string } | null {
    const username = sessionStorage.getItem('username');
    const password = sessionStorage.getItem('password');
    return username && password ? { username, password } : null;
  }
}
