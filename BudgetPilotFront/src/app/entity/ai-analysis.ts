export type FinancialHealth =
  | 'GOOD'
  | 'WARNING'
  | 'CRITICAL';

export type RecommendationPriority =
  | 'HIGH'
  | 'MEDIUM'
  | 'LOW';

export type AnalysisLanguage =
  | 'fr'
  | 'ar';

export interface Recommendation {
  title: string;
  description: string;
  priority: RecommendationPriority;
}

export interface SavingOpportunity {
  category: string;
  estimated_monthly_saving: number;
  action: string;
}

export interface FinancialAnalysis {
  summary: string;
  financial_health: FinancialHealth;
  score: number;
  key_observations: string[];
  recommendations: Recommendation[];
  saving_opportunities: SavingOpportunity[];
  next_month_target: string;
  disclaimer: string;
}

export interface FinancialAnalysisRequest {
  year: number;
  month: number;
  language: AnalysisLanguage;
}
