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
import { InvoiceService } from '../../../core/services/invoice.service';
import { Invoice, InvoiceStatus } from '../../../core/models/invoice.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../../shared/pipes/capitalize.pipe';

@Component({
  selector: 'app-invoices-list',
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
  templateUrl: './invoices-list.component.html',
  styleUrl: './invoices-list.component.scss'
})
export class InvoicesListComponent implements OnInit {
  dataSource = new MatTableDataSource<Invoice>([]);
  invoices: Invoice[] = [];
  filteredInvoices: Invoice[] = [];
  loading = false;
  
  viewMode: 'grid' | 'list' = 'grid';
  searchQuery = '';
  selectedStatus: InvoiceStatus | null = null;

  InvoiceStatus = InvoiceStatus;

  constructor(
    private invoiceService: InvoiceService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadInvoices();
  }

  loadInvoices(): void {
    this.loading = true;
    this.invoiceService.getAll().subscribe({
      next: (invoices) => {
        this.invoices = invoices;
        this.filteredInvoices = invoices;
        this.dataSource.data = invoices;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading invoices:', error);
        this.snackBar.open('Erreur lors du chargement des factures', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
      }
    });
  }

  filterByStatus(status: InvoiceStatus | null): void {
    this.selectedStatus = status;
    this.applyFilters();
  }

  onSearch(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    let filtered = [...this.invoices];

    if (this.selectedStatus) {
      filtered = filtered.filter(i => i.status === this.selectedStatus);
    }

    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(i => 
        (i.invoiceNumber?.toLowerCase().includes(query)) ||
        (i.clientName?.toLowerCase().includes(query)) ||
        (i.client?.companyName?.toLowerCase().includes(query)) ||
        (i.client?.email?.toLowerCase().includes(query))
      );
    }

    this.filteredInvoices = filtered;
  }

  getInvoicesByStatus(status: InvoiceStatus): Invoice[] {
    return this.invoices.filter(i => i.status === status);
  }

  getOverdueInvoices(): Invoice[] {
    return this.invoices.filter(i => i.status === InvoiceStatus.OVERDUE);
  }

  getTotalAmount(): number {
    return this.invoices.reduce((sum, i) => sum + (i.total || 0), 0);
  }

  isOverdue(invoice: Invoice): boolean {
    return invoice.status === InvoiceStatus.OVERDUE;
  }

  getStatusLabel(status: InvoiceStatus): string {
    const labels: Record<InvoiceStatus, string> = {
      DRAFT: 'Brouillon',
      SENT: 'Envoyée',
      PAID: 'Payée',
      OVERDUE: 'En retard',
      CANCELLED: 'Annulée',
      PARTIALLY_PAID: 'Partiellement payée'
    };
    return labels[status] || status;
  }

  viewInvoice(invoice: Invoice): void {
    this.router.navigate(['/invoices', invoice.id]);
  }

  editInvoice(invoice: Invoice): void {
    this.router.navigate(['/invoices', invoice.id, 'edit']);
  }

  deleteInvoice(invoice: Invoice): void {
    const dialogData: ConfirmDialogData = {
      title: 'Supprimer la facture',
      message: `Êtes-vous sûr de vouloir supprimer la facture "${invoice.invoiceNumber}" ?`,
      confirmText: 'Supprimer',
      cancelText: 'Annuler'
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: dialogData,
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && invoice.id) {
        this.invoiceService.delete(invoice.id).subscribe({
          next: () => {
            this.snackBar.open('Facture supprimée avec succès', 'Fermer', {
              duration: 3000
            });
            this.loadInvoices();
          },
          error: (error: any) => {
            console.error('Error deleting invoice:', error);
            this.snackBar.open('Erreur lors de la suppression', 'Fermer', {
              duration: 5000
            });
          }
        });
      }
    });
  }

  downloadPdf(invoice: Invoice): void {
    if (!invoice.id) return;
    
    this.invoiceService.generatePdf(invoice.id).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${invoice.invoiceNumber || 'facture'}.pdf`;
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

  sendByEmail(invoice: Invoice): void {
    if (!invoice.id || !invoice.client?.email) {
      this.snackBar.open('Email du client manquant', 'Fermer', {
        duration: 3000
      });
      return;
    }
    
    this.invoiceService.sendInvoiceByEmail(invoice.id, invoice.client.email).subscribe({
      next: () => {
        this.snackBar.open('Facture envoyée par email avec succès', 'Fermer', {
          duration: 3000
        });
        this.loadInvoices();
      },
      error: (error: any) => {
        console.error('Error sending email:', error);
        this.snackBar.open('Erreur lors de l\'envoi de l\'email', 'Fermer', {
          duration: 5000
        });
      }
    });
  }

  markAsPaid(invoice: Invoice): void {
    if (!invoice.id) return;
    
    const updatedInvoice = { ...invoice, status: InvoiceStatus.PAID, paidDate: new Date().toISOString() };
    this.invoiceService.update(invoice.id, updatedInvoice).subscribe({
      next: () => {
        this.snackBar.open('Facture marquée comme payée', 'Fermer', {
          duration: 3000
        });
        this.loadInvoices();
      },
      error: (error: any) => {
        console.error('Error updating invoice:', error);
        this.snackBar.open('Erreur lors de la mise à jour', 'Fermer', {
          duration: 5000
        });
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
}
