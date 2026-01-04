import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe, DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { QuoteService } from '../../../core/services/quote.service';
import { Quote, QuoteStatus } from '../../../core/models/quote.model';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../../shared/pipes/capitalize.pipe';

@Component({
  selector: 'app-quote-detail',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatChipsModule,
    TranslateModule,
    CapitalizePipe
  ],
  templateUrl: './quote-detail.component.html',
  styleUrl: './quote-detail.component.scss'
})
export class QuoteDetailComponent implements OnInit {
  quote?: Quote;
  loading = false;

  constructor(
    private quoteService: QuoteService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.loadQuote(+id);
    }
  }

  loadQuote(id: number): void {
    this.loading = true;
    this.quoteService.getById(id).subscribe({
      next: (quote) => {
        this.quote = quote;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading quote:', error);
        this.snackBar.open('Erreur lors du chargement du devis', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
        this.router.navigate(['/quotes']);
      }
    });
  }

  formatCurrency(amount?: number): string {
    if (amount === undefined || amount === null) return '-';
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }

  getStatusColor(status: QuoteStatus): string {
    const colors: Record<QuoteStatus, string> = {
      [QuoteStatus.DRAFT]: 'default',
      [QuoteStatus.SENT]: 'primary',
      [QuoteStatus.ACCEPTED]: 'accent',
      [QuoteStatus.REJECTED]: 'warn',
      [QuoteStatus.EXPIRED]: 'warn',
      [QuoteStatus.CONVERTED]: 'accent'
    };
    return colors[status] || 'default';
  }
}
