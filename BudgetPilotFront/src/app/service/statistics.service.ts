import {
  HttpClient,
  HttpParams
} from '@angular/common/http';

import {
  Injectable
} from '@angular/core';

import {
  Observable
} from 'rxjs';

import {
  DashboardStatistics
} from '../entity/statistics';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {

  private readonly apiUrl =
    'http://localhost:8081/api/statistics';

  constructor(
    private http: HttpClient
  ) {}

  getDashboard(
    year: number,
    month: number
  ): Observable<DashboardStatistics> {

    const params =
      new HttpParams()
        .set('year', year)
        .set('month', month);

    return this.http
      .get<DashboardStatistics>(
        `${this.apiUrl}/dashboard`,
        { params }
      );
  }
}
