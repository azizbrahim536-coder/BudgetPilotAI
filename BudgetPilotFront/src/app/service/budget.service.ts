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
  Budget,
  BudgetRequest
} from '../entity/budget';

@Injectable({
  providedIn: 'root'
})
export class BudgetService {

  private readonly apiUrl =
    'http://localhost:8081/api/budgets';

  constructor(
    private http: HttpClient
  ) {}

  getBudgets(
    year: number,
    month: number
  ): Observable<Budget[]> {

    const params =
      new HttpParams()
        .set('year', year)
        .set('month', month);

    return this.http.get<Budget[]>(
      this.apiUrl,
      { params }
    );
  }

  getBudgetById(
    id: number
  ): Observable<Budget> {

    return this.http.get<Budget>(
      `${this.apiUrl}/${id}`
    );
  }

  createBudget(
    request: BudgetRequest
  ): Observable<Budget> {

    return this.http.post<Budget>(
      this.apiUrl,
      request
    );
  }

  updateBudget(
    id: number,
    request: BudgetRequest
  ): Observable<Budget> {

    return this.http.put<Budget>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  deleteBudget(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}
