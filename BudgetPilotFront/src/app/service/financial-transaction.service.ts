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
  FinancialTransaction,
  TransactionFilters,
  TransactionRequest
} from '../entity/financial-transaction';

@Injectable({
  providedIn: 'root'
})
export class FinancialTransactionService {

  private readonly apiUrl =
    'http://localhost:8081/api/transactions';

  constructor(
    private http: HttpClient
  ) {}

  getTransactions(
    filters: TransactionFilters = {}
  ): Observable<FinancialTransaction[]> {

    let params =
      new HttpParams();

    if (filters.search?.trim()) {
      params = params.set(
        'search',
        filters.search.trim()
      );
    }

    if (filters.type) {
      params = params.set(
        'type',
        filters.type
      );
    }

    if (filters.categoryId) {
      params = params.set(
        'categoryId',
        filters.categoryId
      );
    }

    if (filters.dateFrom) {
      params = params.set(
        'dateFrom',
        filters.dateFrom
      );
    }

    if (filters.dateTo) {
      params = params.set(
        'dateTo',
        filters.dateTo
      );
    }

    return this.http
      .get<FinancialTransaction[]>(
        this.apiUrl,
        { params }
      );
  }

  getTransactionById(
    id: number
  ): Observable<FinancialTransaction> {

    return this.http
      .get<FinancialTransaction>(
        `${this.apiUrl}/${id}`
      );
  }

  createTransaction(
    request: TransactionRequest
  ): Observable<FinancialTransaction> {

    return this.http
      .post<FinancialTransaction>(
        this.apiUrl,
        request
      );
  }

  updateTransaction(
    id: number,
    request: TransactionRequest
  ): Observable<FinancialTransaction> {

    return this.http
      .put<FinancialTransaction>(
        `${this.apiUrl}/${id}`,
        request
      );
  }

  deleteTransaction(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}
