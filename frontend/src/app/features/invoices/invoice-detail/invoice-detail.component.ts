import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { InvoiceService } from '../../../core/services/invoice.service';
import { Invoice, InvoiceStatus } from '../../../core/models/invoice.model';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../../shared/pipes/capitalize.pipe';

@Component({
  selector: 'app-invoice-detail',
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
  templateUrl: './invoice-detail.component.html',
  styleUrl: './invoice-detail.component.scss'
})
export class InvoiceDetailComponent implements OnInit {
  invoice?: Invoice;
  loading = false;

  constructor(
    private invoiceService: InvoiceService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.loadInvoice(+id);
    }
  }

  loadInvoice(id: number): void {
    this.loading = true;
    this.invoiceService.getById(id).subscribe({
      next: (invoice) => {
        this.invoice = invoice;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading invoice:', error);
        this.snackBar.open('Erreur lors du chargement de la facture', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
        this.router.navigate(['/invoices']);
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

  getStatusColor(status: InvoiceStatus): string {
    const colors: Record<InvoiceStatus, string> = {
      [InvoiceStatus.DRAFT]: 'default',
      [InvoiceStatus.SENT]: 'primary',
      [InvoiceStatus.PAID]: 'accent',
      [InvoiceStatus.OVERDUE]: 'warn',
      [InvoiceStatus.CANCELLED]: 'warn',
      [InvoiceStatus.PARTIALLY_PAID]: 'primary'
    };
    return colors[status] || 'default';
  }
}
