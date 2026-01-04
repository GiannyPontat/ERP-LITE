export interface DashboardStats {
  totalRevenue: number;
  totalProfit: number;
  unpaidInvoicesCount: number;
  unpaidInvoicesAmount: number;
  activeQuotesCount: number;
  totalClientsCount: number;
  totalQuotesCount: number;
  totalInvoicesCount: number;
}

export interface MonthlyRevenue {
  year: number;
  month: number;
  revenue: number;
}

