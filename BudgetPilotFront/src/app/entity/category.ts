export type TransactionType =
  | 'INCOME'
  | 'EXPENSE';

export interface Category {
  id?: number;
  name: string;
  type: TransactionType;
  icon: string | null;
  color: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CategoryRequest {
  name: string;
  type: TransactionType;
  icon: string | null;
  color: string;
}
