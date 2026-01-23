import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { environment } from '@env/environment';
import { CreateReportDto } from '../models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private base = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  createReport(dto: CreateReportDto): Observable<void> {
    return this.http.post<any>(this.base, dto).pipe(
      map((res) => res?.data ?? res)
    );
  }
}
