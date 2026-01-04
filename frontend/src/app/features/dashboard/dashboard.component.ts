import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe } from '@angular/common';
import { MatCardModule, MatCardHeader, MatCardTitle, MatCardSubtitle, MatCardContent } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatGridListModule } from '@angular/material/grid-list';
import { AuthService } from '../../core/services/auth.service';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardStats, MonthlyRevenue } from '../../core/models/dashboard.model';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../shared/pipes/capitalize.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    DecimalPipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatGridListModule,
    MatCardHeader,
    MatCardTitle,
    MatCardSubtitle,
    MatCardContent,
    TranslateModule,
    CapitalizePipe
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  stats?: DashboardStats;
  monthlyRevenues: MonthlyRevenue[] = [];
  loading = false;
  currentUser$;
  currentYear = new Date().getFullYear();

  constructor(
    public authService: AuthService,
    private dashboardService: DashboardService,
    private snackBar: MatSnackBar
  ) {
    this.currentUser$ = this.authService.currentUser$;
  }

  ngOnInit(): void {
    this.loadStats();
    this.loadMonthlyRevenue();
  }

  loadStats(): void {
    this.loading = true;
    this.dashboardService.getStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading stats:', error);
        this.snackBar.open('Erreur lors du chargement des statistiques', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
      }
    });
  }

  loadMonthlyRevenue(): void {
    const currentYear = new Date().getFullYear();
    this.dashboardService.getMonthlyRevenue(currentYear).subscribe({
      next: (revenues) => {
        this.monthlyRevenues = revenues;
      },
      error: (error) => {
        console.error('Error loading monthly revenue:', error);
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }

  formatCurrency(amount?: number): string {
    if (amount === undefined || amount === null) return '0,00 €';
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }

  getMonthName(month: number): string {
    const months = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun', 'Jul', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'];
    return months[month - 1] || '';
  }

  getBarHeight(revenue: number): number {
    if (!this.monthlyRevenues || this.monthlyRevenues.length === 0) return 0;
    const maxRevenue = Math.max(...this.monthlyRevenues.map(r => r.revenue));
    if (maxRevenue === 0) return 0;
    return Math.max((revenue / maxRevenue) * 100, 5); // Minimum 5% pour visibilité
  }
}
