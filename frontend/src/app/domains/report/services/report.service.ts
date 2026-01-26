import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, throwError } from 'rxjs';

import { environment } from '@env/environment';
import { CreateReportDto } from '../models/report.model';
import { ErrorService } from '@core/services/error.service';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private base = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient, private errorService: ErrorService) {}

  createReport(dto: CreateReportDto): Observable<void> {
    return this.http.post<any>(this.base, dto).pipe(
      map((res) => res?.data ?? res),
      catchError((error) => {
        this.errorService.addError('Unable to submit report.');
        return throwError(() => error);
      })
    );
  }
}
