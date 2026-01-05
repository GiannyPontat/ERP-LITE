import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
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
    FormsModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatTooltipModule,
    MatDialogModule,
    MatMenuModule,
    MatDividerModule,
    TranslateModule,
    CapitalizePipe
  ],
  templateUrl: './quotes-list.component.html',
  styleUrl: './quotes-list.component.scss'
})
export class QuotesListComponent implements OnInit {
  displayedColumns: string[] = ['quoteNumber', 'clientName', 'date', 'status', 'total', 'actions'];
  dataSource = new MatTableDataSource<Quote>([]);
  quotes: Quote[] = [];
  filteredQuotes: Quote[] = [];
  loading = false;
  
  // Nouveaux états pour le design moderne
  viewMode: 'grid' | 'list' = 'grid';
  searchQuery = '';
  selectedStatus: QuoteStatus | null = null;

  // Exposer l'enum pour le template
  QuoteStatus = QuoteStatus;

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
        this.quotes = quotes;
        this.filteredQuotes = quotes;
        this.dataSource.data = quotes;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading quotes:', error);
        this.snackBar.open('Erreur lors du chargement des devis', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
      }
    });
  }

  // Filtrage par statut
  filterByStatus(status: QuoteStatus | null): void {
    this.selectedStatus = status;
    this.applyFilters();
  }

  // Recherche
  onSearch(): void {
    this.applyFilters();
  }

  // Applique tous les filtres
  applyFilters(): void {
    let filtered = [...this.quotes];

    // Filtre par statut
    if (this.selectedStatus) {
      filtered = filtered.filter(q => q.status === this.selectedStatus);
    }

    // Filtre par recherche
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(q => 
        (q.quoteNumber?.toLowerCase().includes(query)) ||
        (q.clientName?.toLowerCase().includes(query)) ||
        (q.client?.companyName?.toLowerCase().includes(query)) ||
        (q.client?.email?.toLowerCase().includes(query))
      );
    }

    this.filteredQuotes = filtered;
  }

  // Obtenir les devis par statut
  getQuotesByStatus(status: QuoteStatus): Quote[] {
    return this.quotes.filter(q => q.status === status);
  }

  // Calculer le montant total
  getTotalAmount(): number {
    return this.quotes.reduce((sum, q) => sum + (q.total || 0), 0);
  }

  // Obtenir le label du statut
  getStatusLabel(status: QuoteStatus): string {
    const labels: Record<QuoteStatus, string> = {
      DRAFT: 'Brouillon',
      SENT: 'Envoyé',
      ACCEPTED: 'Accepté',
      REJECTED: 'Refusé',
      EXPIRED: 'Expiré',
      CONVERTED: 'Converti'
    };
    return labels[status] || status;
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
          error: (error: any) => {
            console.error('Error deleting quote:', error);
            this.snackBar.open('Erreur lors de la suppression', 'Fermer', {
              duration: 5000
            });
          }
        });
      }
    });
  }

  downloadPdf(quote: Quote): void {
    if (!quote.id) return;
    
    this.quoteService.generatePdf(quote.id).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${quote.quoteNumber || 'devis'}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
        
        this.snackBar.open('PDF téléchargé avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error: any) => {
        console.error('Error downloading PDF:', error);
        this.snackBar.open('Erreur lors du téléchargement du PDF', 'Fermer', {
          duration: 5000
        });
      }
    });
  }

  sendByEmail(quote: Quote): void {
    if (!quote.id || !quote.client?.email) {
      this.snackBar.open('Email du client manquant', 'Fermer', {
        duration: 3000
      });
      return;
    }
    
    this.quoteService.sendQuoteByEmail(quote.id, quote.client.email).subscribe({
      next: () => {
        this.snackBar.open('Devis envoyé par email avec succès', 'Fermer', {
          duration: 3000
        });
        this.loadQuotes();
      },
      error: (error: any) => {
        console.error('Error sending email:', error);
        this.snackBar.open('Erreur lors de l\'envoi de l\'email', 'Fermer', {
          duration: 5000
        });
      }
    });
  }

  convertToInvoice(quote: Quote): void {
    if (!quote.id) return;
    
    // Pour l'instant, on navigue juste vers la création de facture avec le quote ID
    this.router.navigate(['/invoices/new'], { queryParams: { quoteId: quote.id } });
  }

  getStatusColor(status: QuoteStatus): string {
    const colors: Record<QuoteStatus, string> = {
      DRAFT: 'default',
      SENT: 'primary',
      ACCEPTED: 'accent',
      REJECTED: 'warn',
      EXPIRED: 'warn',
      CONVERTED: 'accent'
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
