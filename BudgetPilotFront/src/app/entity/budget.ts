import {
  Category
} from './category';

export interface Budget {
  id?: number;
  category: Category;
  limitAmount: number;
  budgetYear: number;
  budgetMonth: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface BudgetRequest {
  categoryId: number;
  limitAmount: number;
  budgetYear: number;
  budgetMonth: number;
}
