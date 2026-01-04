import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { InvoiceService } from '../../../core/services/invoice.service';
import { Invoice, InvoiceStatus } from '../../../core/models/invoice.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-invoices-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatTooltipModule,
    MatDialogModule
  ],
  template: `
    <div class="invoices-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Liste des Factures</mat-card-title>
          <div class="header-actions">
            <button mat-raised-button color="primary" routerLink="/invoices/new">
              <mat-icon>add</mat-icon>
              Nouvelle Facture
            </button>
          </div>
        </mat-card-header>

        <mat-card-content>
          @if (loading) {
            <div class="loading-container">
              <mat-spinner diameter="50"></mat-spinner>
            </div>
          } @else {
            <table mat-table [dataSource]="dataSource" class="mat-elevation-z2">
              <ng-container matColumnDef="invoiceNumber">
                <th mat-header-cell *matHeaderCellDef>Numéro</th>
                <td mat-cell *matCellDef="let invoice">{{ invoice.invoiceNumber || '-' }}</td>
              </ng-container>

              <ng-container matColumnDef="clientName">
                <th mat-header-cell *matHeaderCellDef>Client</th>
                <td mat-cell *matCellDef="let invoice">{{ invoice.clientName || invoice.client?.companyName || '-' }}</td>
              </ng-container>

              <ng-container matColumnDef="date">
                <th mat-header-cell *matHeaderCellDef>Date</th>
                <td mat-cell *matCellDef="let invoice">{{ invoice.date | date:'short' }}</td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>Statut</th>
                <td mat-cell *matCellDef="let invoice">
                  <mat-chip [color]="getStatusColor(invoice.status)">{{ invoice.status }}</mat-chip>
                </td>
              </ng-container>

              <ng-container matColumnDef="total">
                <th mat-header-cell *matHeaderCellDef>Total</th>
                <td mat-cell *matCellDef="let invoice">{{ formatCurrency(invoice.total) }}</td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef>Actions</th>
                <td mat-cell *matCellDef="let invoice">
                  <button mat-icon-button color="primary" (click)="viewInvoice(invoice)" matTooltip="Voir">
                    <mat-icon>visibility</mat-icon>
                  </button>
                  <button mat-icon-button color="accent" (click)="editInvoice(invoice)" matTooltip="Modifier">
                    <mat-icon>edit</mat-icon>
                  </button>
                  <button mat-icon-button color="warn" (click)="deleteInvoice(invoice)" matTooltip="Supprimer">
                    <mat-icon>delete</mat-icon>
                  </button>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>

              <tr class="mat-row" *matNoDataRow>
                <td class="mat-cell" [attr.colspan]="displayedColumns.length">
                  Aucune facture trouvée
                </td>
              </tr>
            </table>
          }
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .invoices-container {
      padding: 20px;
    }

    mat-card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .header-actions {
        display: flex;
        gap: 8px;
      }
    }

    .loading-container {
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 40px;
    }

    table {
      width: 100%;
    }
  `]
})
export class InvoicesListComponent implements OnInit {
  displayedColumns: string[] = ['invoiceNumber', 'clientName', 'date', 'status', 'total', 'actions'];
  dataSource = new MatTableDataSource<Invoice>([]);
  loading = false;

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
        this.dataSource.data = invoices;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading invoices:', error);
        this.snackBar.open('Erreur lors du chargement des factures', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
      }
    });
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
          error: (error) => {
            console.error('Error deleting invoice:', error);
            this.snackBar.open('Erreur lors de la suppression', 'Fermer', {
              duration: 5000
            });
          }
        });
      }
    });
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

  formatCurrency(amount?: number): string {
    if (amount === undefined || amount === null) return '-';
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }
}
