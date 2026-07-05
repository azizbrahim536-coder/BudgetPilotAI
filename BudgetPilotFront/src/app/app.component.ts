import {
  Component,
  OnInit
} from '@angular/core';

import {
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

import {
  forkJoin
} from 'rxjs';

import {
  Category,
  CategoryRequest,
  TransactionType
} from './entity/category';

import {
  FinancialTransaction,
  TransactionFilters,
  TransactionRequest
} from './entity/financial-transaction';

import {
  Budget,
  BudgetRequest
} from './entity/budget';

import {
  DashboardStatistics
} from './entity/statistics';

import {
  CategoryService
} from './service/category.service';

import {
  FinancialTransactionService
} from './service/financial-transaction.service';

import {
  BudgetService
} from './service/budget.service';

import {
  StatisticsService
} from './service/statistics.service';

type AppView =
  | 'dashboard'
  | 'transactions'
  | 'budgets'
  | 'categories';

interface ExpenseBreakdownItem {
  category: Category;
  total: number;
  percentage: number;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: [
    './app.component.css'
  ]
})
export class AppComponent implements OnInit {

  activeView: AppView = 'dashboard';

  categories: Category[] = [];
  incomeCategories: Category[] = [];
  expenseCategories: Category[] = [];

  transactions: FinancialTransaction[] = [];
  allPeriodTransactions: FinancialTransaction[] = [];

  budgets: Budget[] = [];

  statistics: DashboardStatistics = {
    year: new Date().getFullYear(),
    month: new Date().getMonth() + 1,
    totalIncome: 0,
    totalExpense: 0,
    balance: 0,
    transactionCount: 0,
    totalBudget: 0,
    remainingBudget: 0,
    budgetExceeded: false
  };

  transactionForm: FormGroup;
  categoryForm: FormGroup;
  budgetForm: FormGroup;
  filterForm: FormGroup;

  editingTransactionId: number | null = null;
  editingCategoryId: number | null = null;
  editingBudgetId: number | null = null;

  selectedYear = new Date().getFullYear();
  selectedMonth = new Date().getMonth() + 1;

  readonly months = [
    { value: 1, label: 'Janvier' },
    { value: 2, label: 'Février' },
    { value: 3, label: 'Mars' },
    { value: 4, label: 'Avril' },
    { value: 5, label: 'Mai' },
    { value: 6, label: 'Juin' },
    { value: 7, label: 'Juillet' },
    { value: 8, label: 'Août' },
    { value: 9, label: 'Septembre' },
    { value: 10, label: 'Octobre' },
    { value: 11, label: 'Novembre' },
    { value: 12, label: 'Décembre' }
  ];

  readonly years: number[] = [];

  loading = false;
  loadingTransactions = false;

  savingTransaction = false;
  savingCategory = false;
  savingBudget = false;

  errorMessage = '';
  successMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    private transactionService: FinancialTransactionService,
    private budgetService: BudgetService,
    private statisticsService: StatisticsService
  ) {
    const currentYear =
      new Date().getFullYear();

    for (
      let year = currentYear - 4;
      year <= currentYear + 4;
      year++
    ) {
      this.years.push(year);
    }

    this.transactionForm =
      this.formBuilder.group({
        title: [
          '',
          [
            Validators.required,
            Validators.maxLength(150)
          ]
        ],

        description: [
          '',
          Validators.maxLength(3000)
        ],

        amount: [
          null,
          [
            Validators.required,
            Validators.min(0.01)
          ]
        ],

        type: [
          'EXPENSE',
          Validators.required
        ],

        categoryId: [
          null,
          Validators.required
        ],

        transactionDate: [
          this.getToday(),
          Validators.required
        ]
      });

    this.categoryForm =
      this.formBuilder.group({
        name: [
          '',
          [
            Validators.required,
            Validators.maxLength(80)
          ]
        ],

        type: [
          'EXPENSE',
          Validators.required
        ],

        icon: [
          'tag',
          Validators.maxLength(50)
        ],

        color: [
          '#6366F1',
          [
            Validators.required,
            Validators.pattern(
              /^#[0-9A-Fa-f]{6}$/
            )
          ]
        ]
      });

    this.budgetForm =
      this.formBuilder.group({
        categoryId: [
          null,
          Validators.required
        ],

        limitAmount: [
          null,
          [
            Validators.required,
            Validators.min(0.01)
          ]
        ],

        budgetYear: [
          this.selectedYear,
          Validators.required
        ],

        budgetMonth: [
          this.selectedMonth,
          Validators.required
        ]
      });

    this.filterForm =
      this.formBuilder.group({
        search: [''],
        type: [''],
        categoryId: [null],
        dateFrom: [
          this.getPeriodStart()
        ],
        dateTo: [
          this.getPeriodEnd()
        ]
      });

    this.configureFormListeners();
  }

  ngOnInit(): void {
    this.loadAllData();
  }

  setView(view: AppView): void {
    this.activeView = view;
    this.errorMessage = '';
    this.successMessage = '';
  }

  changePeriod(): void {
    this.filterForm.patchValue({
      dateFrom: this.getPeriodStart(),
      dateTo: this.getPeriodEnd()
    });

    if (this.editingBudgetId === null) {
      this.budgetForm.patchValue({
        budgetYear: this.selectedYear,
        budgetMonth: this.selectedMonth
      });
    }

    this.loadAllData();
  }

  loadAllData(): void {
    this.loading = true;
    this.errorMessage = '';

    const periodFilters:
      TransactionFilters = {
        dateFrom: this.getPeriodStart(),
        dateTo: this.getPeriodEnd()
      };

    forkJoin({
      categories:
        this.categoryService
          .getCategories(),

      transactions:
        this.transactionService
          .getTransactions(periodFilters),

      budgets:
        this.budgetService
          .getBudgets(
            this.selectedYear,
            this.selectedMonth
          ),

      statistics:
        this.statisticsService
          .getDashboard(
            this.selectedYear,
            this.selectedMonth
          )
    }).subscribe({
      next: response => {
        this.categories =
          response.categories;

        this.incomeCategories =
          this.categories.filter(
            category =>
              category.type === 'INCOME'
          );

        this.expenseCategories =
          this.categories.filter(
            category =>
              category.type === 'EXPENSE'
          );

        this.transactions =
          response.transactions;

        this.allPeriodTransactions =
          response.transactions;

        this.budgets =
          response.budgets;

        this.statistics =
          response.statistics;

        this.loading = false;
      },

      error: error => {
        console.error(
          'Erreur de chargement :',
          error
        );

        this.errorMessage =
          this.extractError(
            error,
            'Impossible de charger les données. Vérifiez que le backend fonctionne.'
          );

        this.loading = false;
      }
    });
  }

  saveTransaction(): void {
    this.clearMessages();

    if (this.transactionForm.invalid) {
      this.transactionForm
        .markAllAsTouched();

      return;
    }

    const value =
      this.transactionForm
        .getRawValue();

    const request:
      TransactionRequest = {
        title:
          String(value.title).trim(),

        description:
          String(
            value.description ?? ''
          ).trim() || null,

        amount:
          Number(value.amount),

        type:
          value.type as TransactionType,

        categoryId:
          Number(value.categoryId),

        transactionDate:
          String(value.transactionDate)
      };

    this.savingTransaction = true;

    const operation =
      this.editingTransactionId === null

        ? this.transactionService
            .createTransaction(request)

        : this.transactionService
            .updateTransaction(
              this.editingTransactionId,
              request
            );

    operation.subscribe({
      next: () => {
        const wasEditing =
          this.editingTransactionId !== null;

        this.resetTransactionForm();

        this.showSuccess(
          wasEditing
            ? 'La transaction a été modifiée.'
            : 'La transaction a été ajoutée.'
        );

        this.savingTransaction = false;
        this.loadAllData();
      },

      error: error => {
        console.error(error);

        this.errorMessage =
          this.extractError(
            error,
            'Impossible d’enregistrer la transaction.'
          );

        this.savingTransaction = false;
      }
    });
  }

  editTransaction(
    transaction: FinancialTransaction
  ): void {
    if (transaction.id === undefined) {
      return;
    }

    this.editingTransactionId =
      transaction.id;

    this.transactionForm.patchValue({
      title:
        transaction.title,

      description:
        transaction.description ?? '',

      amount:
        transaction.amount,

      type:
        transaction.type,

      categoryId:
        transaction.category.id,

      transactionDate:
        transaction.transactionDate
    });

    this.setView('transactions');
    this.scrollToSection(
      'transaction-form'
    );
  }

  deleteTransaction(
    transaction: FinancialTransaction
  ): void {
    if (transaction.id === undefined) {
      return;
    }

    const confirmed =
      window.confirm(
        `Supprimer la transaction « ${transaction.title} » ?`
      );

    if (!confirmed) {
      return;
    }

    this.transactionService
      .deleteTransaction(
        transaction.id
      )
      .subscribe({
        next: () => {
          if (
            this.editingTransactionId ===
            transaction.id
          ) {
            this.resetTransactionForm();
          }

          this.showSuccess(
            'La transaction a été supprimée.'
          );

          this.loadAllData();
        },

        error: error => {
          this.errorMessage =
            this.extractError(
              error,
              'Impossible de supprimer la transaction.'
            );
        }
      });
  }

  resetTransactionForm(): void {
    this.editingTransactionId = null;

    this.transactionForm.reset({
      title: '',
      description: '',
      amount: null,
      type: 'EXPENSE',
      categoryId: null,
      transactionDate:
        this.getToday()
    });
  }

  applyTransactionFilters(): void {
    this.clearMessages();
    this.loadingTransactions = true;

    const value =
      this.filterForm.getRawValue();

    const filters:
      TransactionFilters = {
        search:
          String(
            value.search ?? ''
          ).trim(),

        type:
          value.type as
            TransactionType | '',

        categoryId:
          value.categoryId
            ? Number(value.categoryId)
            : null,

        dateFrom:
          value.dateFrom || '',

        dateTo:
          value.dateTo || ''
      };

    this.transactionService
      .getTransactions(filters)
      .subscribe({
        next: transactions => {
          this.transactions =
            transactions;

          this.loadingTransactions =
            false;
        },

        error: error => {
          this.errorMessage =
            this.extractError(
              error,
              'Impossible de filtrer les transactions.'
            );

          this.loadingTransactions =
            false;
        }
      });
  }

  clearTransactionFilters(): void {
    this.filterForm.reset({
      search: '',
      type: '',
      categoryId: null,
      dateFrom:
        this.getPeriodStart(),
      dateTo:
        this.getPeriodEnd()
    });

    this.transactions =
      this.allPeriodTransactions;
  }

  saveCategory(): void {
    this.clearMessages();

    if (this.categoryForm.invalid) {
      this.categoryForm
        .markAllAsTouched();

      return;
    }

    const value =
      this.categoryForm
        .getRawValue();

    const request:
      CategoryRequest = {
        name:
          String(value.name).trim(),

        type:
          value.type as TransactionType,

        icon:
          String(
            value.icon ?? ''
          ).trim() || null,

        color:
          String(value.color)
            .trim()
            .toUpperCase()
      };

    this.savingCategory = true;

    const operation =
      this.editingCategoryId === null

        ? this.categoryService
            .createCategory(request)

        : this.categoryService
            .updateCategory(
              this.editingCategoryId,
              request
            );

    operation.subscribe({
      next: () => {
        const wasEditing =
          this.editingCategoryId !== null;

        this.resetCategoryForm();

        this.showSuccess(
          wasEditing
            ? 'La catégorie a été modifiée.'
            : 'La catégorie a été ajoutée.'
        );

        this.savingCategory = false;
        this.loadAllData();
      },

      error: error => {
        this.errorMessage =
          this.extractError(
            error,
            'Impossible d’enregistrer la catégorie.'
          );

        this.savingCategory = false;
      }
    });
  }

  editCategory(
    category: Category
  ): void {
    if (category.id === undefined) {
      return;
    }

    this.editingCategoryId =
      category.id;

    this.categoryForm.patchValue({
      name:
        category.name,

      type:
        category.type,

      icon:
        category.icon ?? 'tag',

      color:
        category.color
    });

    this.setView('categories');
    this.scrollToSection(
      'category-form'
    );
  }

  deleteCategory(
    category: Category
  ): void {
    if (category.id === undefined) {
      return;
    }

    const confirmed =
      window.confirm(
        `Supprimer la catégorie « ${category.name} » ?`
      );

    if (!confirmed) {
      return;
    }

    this.categoryService
      .deleteCategory(
        category.id
      )
      .subscribe({
        next: () => {
          if (
            this.editingCategoryId ===
            category.id
          ) {
            this.resetCategoryForm();
          }

          this.showSuccess(
            'La catégorie a été supprimée.'
          );

          this.loadAllData();
        },

        error: error => {
          this.errorMessage =
            this.extractError(
              error,
              'Cette catégorie est peut-être déjà utilisée.'
            );
        }
      });
  }

  resetCategoryForm(): void {
    this.editingCategoryId = null;

    this.categoryForm.reset({
      name: '',
      type: 'EXPENSE',
      icon: 'tag',
      color: '#6366F1'
    });
  }

  saveBudget(): void {
    this.clearMessages();

    if (this.budgetForm.invalid) {
      this.budgetForm.markAllAsTouched();
      return;
    }

    const value =
      this.budgetForm.getRawValue();

    const request:
      BudgetRequest = {
        categoryId:
          Number(value.categoryId),

        limitAmount:
          Number(value.limitAmount),

        budgetYear:
          Number(value.budgetYear),

        budgetMonth:
          Number(value.budgetMonth)
      };

    this.savingBudget = true;

    const operation =
      this.editingBudgetId === null

        ? this.budgetService
            .createBudget(request)

        : this.budgetService
            .updateBudget(
              this.editingBudgetId,
              request
            );

    operation.subscribe({
      next: () => {
        const wasEditing =
          this.editingBudgetId !== null;

        this.resetBudgetForm();

        this.showSuccess(
          wasEditing
            ? 'Le budget a été modifié.'
            : 'Le budget a été ajouté.'
        );

        this.savingBudget = false;
        this.loadAllData();
      },

      error: error => {
        this.errorMessage =
          this.extractError(
            error,
            'Impossible d’enregistrer le budget.'
          );

        this.savingBudget = false;
      }
    });
  }

  editBudget(
    budget: Budget
  ): void {
    if (budget.id === undefined) {
      return;
    }

    this.editingBudgetId =
      budget.id;

    this.budgetForm.patchValue({
      categoryId:
        budget.category.id,

      limitAmount:
        budget.limitAmount,

      budgetYear:
        budget.budgetYear,

      budgetMonth:
        budget.budgetMonth
    });

    this.setView('budgets');
    this.scrollToSection(
      'budget-form'
    );
  }

  deleteBudget(
    budget: Budget
  ): void {
    if (budget.id === undefined) {
      return;
    }

    const confirmed =
      window.confirm(
        `Supprimer le budget « ${budget.category.name} » ?`
      );

    if (!confirmed) {
      return;
    }

    this.budgetService
      .deleteBudget(
        budget.id
      )
      .subscribe({
        next: () => {
          if (
            this.editingBudgetId ===
            budget.id
          ) {
            this.resetBudgetForm();
          }

          this.showSuccess(
            'Le budget a été supprimé.'
          );

          this.loadAllData();
        },

        error: error => {
          this.errorMessage =
            this.extractError(
              error,
              'Impossible de supprimer le budget.'
            );
        }
      });
  }

  resetBudgetForm(): void {
    this.editingBudgetId = null;

    this.budgetForm.reset({
      categoryId: null,
      limitAmount: null,
      budgetYear:
        this.selectedYear,
      budgetMonth:
        this.selectedMonth
    });
  }

  get transactionCategoryOptions():
    Category[] {

    const type =
      this.transactionForm
        .get('type')
        ?.value as TransactionType;

    return type === 'INCOME'
      ? this.incomeCategories
      : this.expenseCategories;
  }

  get filterCategoryOptions():
    Category[] {

    const type =
      this.filterForm
        .get('type')
        ?.value as
          TransactionType | '';

    if (type === 'INCOME') {
      return this.incomeCategories;
    }

    if (type === 'EXPENSE') {
      return this.expenseCategories;
    }

    return this.categories;
  }

  get recentTransactions():
    FinancialTransaction[] {

    return this.allPeriodTransactions
      .slice(0, 5);
  }

  get expenseBreakdown():
    ExpenseBreakdownItem[] {

    const totalExpense =
      this.allPeriodTransactions
        .filter(
          transaction =>
            transaction.type ===
            'EXPENSE'
        )
        .reduce(
          (total, transaction) =>
            total +
            Number(transaction.amount),
          0
        );

    if (totalExpense === 0) {
      return [];
    }

    return this.expenseCategories
      .map(category => {
        const total =
          this.allPeriodTransactions
            .filter(
              transaction =>
                transaction.type ===
                  'EXPENSE'
                &&
                transaction.category.id ===
                  category.id
            )
            .reduce(
              (
                categoryTotal,
                transaction
              ) =>
                categoryTotal +
                Number(
                  transaction.amount
                ),
              0
            );

        return {
          category,
          total,
          percentage:
            (
              total /
              totalExpense
            ) * 100
        };
      })
      .filter(
        item => item.total > 0
      )
      .sort(
        (first, second) =>
          second.total -
          first.total
      );
  }

  getBudgetSpent(
    budget: Budget
  ): number {
    return this.allPeriodTransactions
      .filter(
        transaction =>
          transaction.type ===
            'EXPENSE'
          &&
          transaction.category.id ===
            budget.category.id
      )
      .reduce(
        (total, transaction) =>
          total +
          Number(transaction.amount),
        0
      );
  }

  getBudgetRemaining(
    budget: Budget
  ): number {
    return Number(
      budget.limitAmount
    ) - this.getBudgetSpent(budget);
  }

  getBudgetPercentage(
    budget: Budget
  ): number {
    const limit =
      Number(budget.limitAmount);

    if (limit <= 0) {
      return 0;
    }

    return Math.min(
      (
        this.getBudgetSpent(budget) /
        limit
      ) * 100,
      100
    );
  }

  isBudgetExceeded(
    budget: Budget
  ): boolean {
    return this.getBudgetSpent(budget) >
      Number(budget.limitAmount);
  }

  formatAmount(
    amount: number
  ): string {
    return new Intl.NumberFormat(
      'fr-TN',
      {
        style: 'currency',
        currency: 'TND',
        minimumFractionDigits: 2
      }
    ).format(Number(amount || 0));
  }

  getCategoryIcon(
    category: Category
  ): string {
    const icons:
      Record<string, string> = {
        wallet: '💼',
        laptop: '💻',
        gift: '🎁',
        utensils: '🍽️',
        car: '🚗',
        house: '🏠',
        heart: '❤️',
        gamepad: '🎮',
        'shopping-bag': '🛍️',
        tag: '🏷️'
      };

    return icons[
      category.icon ?? 'tag'
    ] ?? '🏷️';
  }

  trackById(
    _index: number,
    item: {
      id?: number;
    }
  ): number | undefined {
    return item.id;
  }

  private configureFormListeners():
    void {

    this.transactionForm
      .get('type')
      ?.valueChanges
      .subscribe(() => {
        const categoryId =
          Number(
            this.transactionForm
              .get('categoryId')
              ?.value
          );

        const valid =
          this.transactionCategoryOptions
            .some(
              category =>
                category.id ===
                categoryId
            );

        if (!valid) {
          this.transactionForm
            .get('categoryId')
            ?.setValue(null);
        }
      });

    this.filterForm
      .get('type')
      ?.valueChanges
      .subscribe(() => {
        const categoryId =
          Number(
            this.filterForm
              .get('categoryId')
              ?.value
          );

        const valid =
          this.filterCategoryOptions
            .some(
              category =>
                category.id ===
                categoryId
            );

        if (!valid) {
          this.filterForm
            .get('categoryId')
            ?.setValue(null);
        }
      });
  }

  private getPeriodStart(): string {
    return [
      this.selectedYear,
      String(
        this.selectedMonth
      ).padStart(2, '0'),
      '01'
    ].join('-');
  }

  private getPeriodEnd(): string {
    const lastDay =
      new Date(
        this.selectedYear,
        this.selectedMonth,
        0
      ).getDate();

    return [
      this.selectedYear,
      String(
        this.selectedMonth
      ).padStart(2, '0'),
      String(lastDay)
        .padStart(2, '0')
    ].join('-');
  }

  private getToday(): string {
    const today = new Date();

    return [
      today.getFullYear(),
      String(
        today.getMonth() + 1
      ).padStart(2, '0'),
      String(
        today.getDate()
      ).padStart(2, '0')
    ].join('-');
  }

  private scrollToSection(
    id: string
  ): void {
    window.setTimeout(() => {
      document
        .getElementById(id)
        ?.scrollIntoView({
          behavior: 'smooth',
          block: 'start'
        });
    });
  }

  private clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  private showSuccess(
    message: string
  ): void {
    this.errorMessage = '';
    this.successMessage = message;

    window.setTimeout(() => {
      if (
        this.successMessage ===
        message
      ) {
        this.successMessage = '';
      }
    }, 4000);
  }

  private extractError(
    error: any,
    fallback: string
  ): string {
    if (
      Array.isArray(
        error?.error?.errors
      )
      &&
      error.error.errors.length > 0
    ) {
      return error.error.errors.join(
        ' '
      );
    }

    return (
      error?.error?.message ||
      fallback
    );
  }
}
