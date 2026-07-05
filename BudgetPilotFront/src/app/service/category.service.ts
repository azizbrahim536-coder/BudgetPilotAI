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
  Category,
  CategoryRequest,
  TransactionType
} from '../entity/category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private readonly apiUrl =
    'http://localhost:8081/api/categories';

  constructor(
    private http: HttpClient
  ) {}

  getCategories(
    type?: TransactionType
  ): Observable<Category[]> {

    let params =
      new HttpParams();

    if (type) {
      params = params.set(
        'type',
        type
      );
    }

    return this.http.get<Category[]>(
      this.apiUrl,
      { params }
    );
  }

  getCategoryById(
    id: number
  ): Observable<Category> {

    return this.http.get<Category>(
      `${this.apiUrl}/${id}`
    );
  }

  createCategory(
    request: CategoryRequest
  ): Observable<Category> {

    return this.http.post<Category>(
      this.apiUrl,
      request
    );
  }

  updateCategory(
    id: number,
    request: CategoryRequest
  ): Observable<Category> {

    return this.http.put<Category>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  deleteCategory(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}
