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
  private apiUrl = environment.apiUrl + '/area';

  private resultsSubject = new BehaviorSubject<IPoint[]>([]);
  public results$: Observable<IPoint[]> = this.resultsSubject.asObservable();

  public totalElements = new BehaviorSubject<number>(0);

  constructor(private http: HttpClient) { }

  loadHistory(page: number = 0, size: number = 5): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/history?page=${page}&size=${size}`).pipe(
      tap(response => {
        // Spring's Page object structure
        this.resultsSubject.next(response.content);
        this.totalElements.next(response.totalElements);
      })
    );
  }

  checkPoint(request: PointRequest): Observable<IPoint> {
    return this.http.post<IPoint>(`${this.apiUrl}/check`, request).pipe(
      tap(() => {
        // Refresh the list from server to ensure pagination and data are sync'd
        this.loadHistory(0, 5).subscribe();
      })
    );
  }

 clearHistory(currentUsername: string): Observable<any> {
   return this.http.delete(`${this.apiUrl}/history`).pipe(
     tap(() => {
       // Get the current list of all points
       const allResults = this.resultsSubject.value;

       // Filter out ONLY the points belonging to the user who clicked clear
       const remainingResults = allResults.filter(p => p.username !== currentUsername);

       // Update the state with only the other users' points
       this.resultsSubject.next(remainingResults);

       // Optionally refresh from server to get accurate pagination counts
       this.loadHistory(0, 5).subscribe();
     })
   );
 }

  validateR(r: number | null): boolean {
    return r !== null && r > 0;
  }

  validateY(y: any): boolean {
    const val = parseFloat(y);
    return !isNaN(val) && val >= -3 && val <= 3;
  }
}
