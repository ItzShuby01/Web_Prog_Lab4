import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { IPoint, PointRequest } from '../models/IPoint';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AreaService {
  private apiUrl = environment.apiUrl + '/area'; // /api/area

  // State management: Use BehaviorSubject to hold and share the list of results
  private resultsSubject = new BehaviorSubject<IPoint[]>([]);
  public results$: Observable<IPoint[]> = this.resultsSubject.asObservable();

  constructor(private http: HttpClient) { }

  // Fetch history from Spring's GET /api/area/history
  loadHistory(): Observable<IPoint[]> {
    return this.http.get<IPoint[]>(`${this.apiUrl}/history`).pipe(
      tap(points => {
        // Update the shared state with the fetched history, newest first
        this.resultsSubject.next(points.reverse());
      })
    );
  }

  // Submit a new point to Spring's POST /api/area/check
  checkPoint(request: PointRequest): Observable<IPoint> {
    return this.http.post<IPoint>(`${this.apiUrl}/check`, request).pipe(
      tap(newPoint => {
        // Add the new result to the start of the list and update the state
        const currentResults = this.resultsSubject.getValue();
        this.resultsSubject.next([newPoint, ...currentResults]);
      })
    );
  }

  // Clear history via Spring's DELETE /api/area/history
  clearHistory(): Observable<any> {
    return this.http.delete(`${this.apiUrl}/history`).pipe(
      tap(() => {
        // Clear the shared state upon successful database deletion
        this.resultsSubject.next([]);
      })
    );
  }

  // --- Client-Side Geometry Logic (For Canvas Helper/Preview) ---
  isHit(x: number, y: number, r: number): boolean {
    if (r <= 0 || isNaN(r)) return false;

    // R1: Rectangle [0, R/2] x [0, R]
    const region1 = (x >= 0 && x <= r / 2.0) && (y >= 0 && y <= r);

    // R2: Trapezoid in Q2: x < 0. Bounded by x=-R/2, y=R, and line y=x+R/2.
    const region2 = (x < 0 && x >= -r / 2.0) && (y >= 0 && y <= r) && (y >= x + r / 2.0);

    // R3: Quarter Circle [0, R/2] x [-R/2, 0] centered at (0,0) with radius R/2
    const region3 = (x >= 0 && y <= 0) && (x * x + y * y <= (r / 2.0) * (r / 2.0));

    return region1 || region2 || region3;
  }

  // --- Client-Side Validation Logic ---

  validateR(r: number | null): boolean {
    const rOptions = [1.0, 2.0, 3.0, 4.0, 5.0];
    return r !== null && rOptions.includes(r);
  }

  validateY(y: number | null): boolean {
    // For form submissions, Y must be between -3 and 3
    return y !== null && y >= -3.0 && y <= 3.0;
  }
}
