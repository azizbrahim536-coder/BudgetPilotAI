import {
  Category,
  TransactionType
} from './category';

export interface FinancialTransaction {
  id?: number;
  title: string;
  description: string | null;
  amount: number;
  type: TransactionType;
  category: Category;
  transactionDate: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface TransactionRequest {
  title: string;
  description: string | null;
  amount: number;
  type: TransactionType;
  categoryId: number;
  transactionDate: string;
}

export interface TransactionFilters {
  search?: string;
  type?: TransactionType | '';
  categoryId?: number | null;
  dateFrom?: string;
  dateTo?: string;
}
