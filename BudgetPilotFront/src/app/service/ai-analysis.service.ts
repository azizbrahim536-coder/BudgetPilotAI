  import {
  HttpClient
} from '@angular/common/http';

import {
  Injectable
} from '@angular/core';

import {
  Observable
} from 'rxjs';

import {
  FinancialAnalysis,
  FinancialAnalysisRequest
} from '../entity/ai-analysis';

@Injectable({
  providedIn: 'root'
})
export class AiAnalysisService {

  private readonly apiUrl =
    'http://localhost:5003/api/ai';

  constructor(
    private http: HttpClient
  ) {
  }

  analyzeFinances(
    request: FinancialAnalysisRequest
  ): Observable<FinancialAnalysis> {

    return this.http.post<FinancialAnalysis>(
      `${this.apiUrl}/analyze`,
      request
    );
  }
}
