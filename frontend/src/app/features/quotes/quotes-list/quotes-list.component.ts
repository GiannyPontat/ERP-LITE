import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { QuoteService } from '../../../core/services/quote.service';
import { Quote, QuoteStatus } from '../../../core/models/quote.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../../shared/pipes/capitalize.pipe';

@Component({
  selector: 'app-quotes-list',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    RouterLink,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatTooltipModule,
    MatDialogModule,
    TranslateModule,
    CapitalizePipe
  ],
  templateUrl: './quotes-list.component.html',
  styleUrl: './quotes-list.component.scss'
})
export class QuotesListComponent implements OnInit {
  displayedColumns: string[] = ['quoteNumber', 'clientName', 'date', 'status', 'total', 'actions'];
  dataSource = new MatTableDataSource<Quote>([]);
  loading = false;

  constructor(
    private quoteService: QuoteService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadQuotes();
  }

  loadQuotes(): void {
    this.loading = true;
    this.quoteService.getAll().subscribe({
      next: (quotes) => {
        this.dataSource.data = quotes;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading quotes:', error);
        this.snackBar.open('Erreur lors du chargement des devis', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
      }
    });
  }

  viewQuote(quote: Quote): void {
    this.router.navigate(['/quotes', quote.id]);
  }

  editQuote(quote: Quote): void {
    this.router.navigate(['/quotes', quote.id, 'edit']);
  }

  deleteQuote(quote: Quote): void {
    const dialogData: ConfirmDialogData = {
      title: 'Supprimer le devis',
      message: `Êtes-vous sûr de vouloir supprimer le devis "${quote.quoteNumber}" ?`,
      confirmText: 'Supprimer',
      cancelText: 'Annuler'
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: dialogData,
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && quote.id) {
        this.quoteService.delete(quote.id).subscribe({
          next: () => {
            this.snackBar.open('Devis supprimé avec succès', 'Fermer', {
              duration: 3000
            });
            this.loadQuotes();
          },
          error: (error) => {
            console.error('Error deleting quote:', error);
            this.snackBar.open('Erreur lors de la suppression', 'Fermer', {
              duration: 5000
            });
          }
        });
      }
    });
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

  formatCurrency(amount?: number): string {
    if (amount === undefined || amount === null) return '-';
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }
}
