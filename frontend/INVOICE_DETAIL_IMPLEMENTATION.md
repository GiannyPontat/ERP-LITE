# Implémentation InvoiceDetailComponent

## Fonctionnalités à ajouter

Ce fichier montre comment implémenter les nouvelles fonctionnalités dans le composant InvoiceDetailComponent.

## Code TypeScript

```typescript
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { InvoiceService } from '../../core/services/invoice.service';
import { Invoice } from '../../core/models/invoice.model';

@Component({
  selector: 'app-invoice-detail',
  templateUrl: './invoice-detail.component.html',
  styleUrls: ['./invoice-detail.component.scss']
})
export class InvoiceDetailComponent implements OnInit {
  invoice?: Invoice;
  loading = false;
  sendingEmail = false;
  sendingReminder = false;
  markingAsPaid = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private invoiceService: InvoiceService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.loadInvoice(id);
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
      }
    });
  }

  /**
   * Télécharge le PDF de la facture
   */
  downloadPdf(): void {
    if (!this.invoice) return;

    this.invoiceService.generatePdf(this.invoice.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `facture-${this.invoice!.invoiceNumber}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);

        this.snackBar.open('PDF téléchargé avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error) => {
        console.error('Error downloading PDF:', error);
        this.snackBar.open('Erreur lors du téléchargement du PDF', 'Fermer', {
          duration: 5000
        });
      }
    });
  }

  /**
   * Ouvre un dialog pour envoyer la facture par email
   */
  openSendEmailDialog(): void {
    if (!this.invoice) return;

    const dialogRef = this.dialog.open(SendEmailDialogComponent, {
      width: '400px',
      data: {
        title: 'Envoyer la facture par email',
        recipientEmail: this.invoice.client?.email || ''
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.email) {
        this.sendEmail(result.email);
      }
    });
  }

  /**
   * Envoie la facture par email
   */
  sendEmail(recipientEmail: string): void {
    if (!this.invoice) return;

    this.sendingEmail = true;
    this.invoiceService.sendInvoiceByEmail(this.invoice.id, recipientEmail).subscribe({
      next: (response) => {
        this.snackBar.open('Facture envoyée avec succès', 'Fermer', {
          duration: 3000
        });
        this.sendingEmail = false;
        this.loadInvoice(this.invoice!.id);
      },
      error: (error) => {
        console.error('Error sending email:', error);
        this.snackBar.open('Erreur lors de l\'envoi de l\'email', 'Fermer', {
          duration: 5000
        });
        this.sendingEmail = false;
      }
    });
  }

  /**
   * Ouvre un dialog pour envoyer une relance
   */
  openSendReminderDialog(): void {
    if (!this.invoice) return;

    // Vérifier que la facture n'est pas payée
    if (this.invoice.status === 'PAID') {
      this.snackBar.open('Cette facture est déjà payée', 'Fermer', {
        duration: 3000
      });
      return;
    }

    const dialogRef = this.dialog.open(SendEmailDialogComponent, {
      width: '400px',
      data: {
        title: 'Envoyer une relance de paiement',
        recipientEmail: this.invoice.client?.email || ''
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.email) {
        this.sendReminder(result.email);
      }
    });
  }

  /**
   * Envoie une relance pour facture impayée
   */
  sendReminder(recipientEmail: string): void {
    if (!this.invoice) return;

    this.sendingReminder = true;
    this.invoiceService.sendInvoiceReminder(this.invoice.id, recipientEmail).subscribe({
      next: (response) => {
        this.snackBar.open('Relance envoyée avec succès', 'Fermer', {
          duration: 3000
        });
        this.sendingReminder = false;
      },
      error: (error) => {
        console.error('Error sending reminder:', error);
        this.snackBar.open('Erreur lors de l\'envoi de la relance', 'Fermer', {
          duration: 5000
        });
        this.sendingReminder = false;
      }
    });
  }

  /**
   * Ouvre un dialog pour marquer comme payée
   */
  openMarkAsPaidDialog(): void {
    if (!this.invoice) return;

    // Vérifier que la facture n'est pas déjà payée
    if (this.invoice.status === 'PAID') {
      this.snackBar.open('Cette facture est déjà marquée comme payée', 'Fermer', {
        duration: 3000
      });
      return;
    }

    const dialogRef = this.dialog.open(MarkAsPaidDialogComponent, {
      width: '400px',
      data: {
        invoiceNumber: this.invoice.invoiceNumber
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.markAsPaid(result.paidDate);
      }
    });
  }

  /**
   * Marque la facture comme payée
   */
  markAsPaid(paidDate?: string): void {
    if (!this.invoice) return;

    this.markingAsPaid = true;
    this.invoiceService.markAsPaid(this.invoice.id, paidDate).subscribe({
      next: (updatedInvoice) => {
        this.invoice = updatedInvoice;
        this.snackBar.open('Facture marquée comme payée', 'Fermer', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.markingAsPaid = false;
      },
      error: (error) => {
        console.error('Error marking as paid:', error);
        this.snackBar.open('Erreur lors de la mise à jour du statut', 'Fermer', {
          duration: 5000
        });
        this.markingAsPaid = false;
      }
    });
  }

  /**
   * Vérifie si le bouton relance doit être visible
   */
  canSendReminder(): boolean {
    if (!this.invoice) return false;
    return this.invoice.status === 'OVERDUE' || this.invoice.status === 'SENT';
  }

  /**
   * Vérifie si le bouton marquer comme payée doit être visible
   */
  canMarkAsPaid(): boolean {
    if (!this.invoice) return false;
    return this.invoice.status !== 'PAID';
  }
}
```

## Template HTML - Boutons à ajouter

```html
<!-- Dans le header de la facture -->
<div class="invoice-actions">
  <!-- Bouton Télécharger PDF -->
  <button mat-raised-button color="primary" (click)="downloadPdf()" [disabled]="loading">
    <mat-icon>download</mat-icon>
    Télécharger PDF
  </button>

  <!-- Bouton Envoyer par email -->
  <button mat-raised-button color="accent" (click)="openSendEmailDialog()" [disabled]="loading || sendingEmail">
    <mat-icon>email</mat-icon>
    {{ sendingEmail ? 'Envoi...' : 'Envoyer par email' }}
  </button>

  <!-- Bouton Envoyer relance (visible seulement si OVERDUE ou SENT) -->
  <button mat-raised-button color="warn"
          (click)="openSendReminderDialog()"
          [disabled]="loading || sendingReminder"
          *ngIf="canSendReminder()">
    <mat-icon>notification_important</mat-icon>
    {{ sendingReminder ? 'Envoi...' : 'Relancer' }}
  </button>

  <!-- Bouton Marquer comme payée (visible seulement si pas PAID) -->
  <button mat-raised-button color="primary"
          (click)="openMarkAsPaidDialog()"
          [disabled]="loading || markingAsPaid"
          *ngIf="canMarkAsPaid()">
    <mat-icon>check_circle</mat-icon>
    {{ markingAsPaid ? 'Mise à jour...' : 'Marquer comme payée' }}
  </button>
</div>

<!-- Badge de statut avec style conditionnel -->
<div class="invoice-status">
  <span class="status-badge"
        [ngClass]="{
          'status-paid': invoice?.status === 'PAID',
          'status-pending': invoice?.status === 'SENT',
          'status-overdue': invoice?.status === 'OVERDUE'
        }">
    {{ invoice?.status }}
  </span>
</div>
```

## Dialog Component pour marquer comme payée

```typescript
// mark-as-paid-dialog.component.ts
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-mark-as-paid-dialog',
  template: `
    <h2 mat-dialog-title>Marquer la facture comme payée</h2>
    <mat-dialog-content>
      <p>Marquer la facture <strong>{{ data.invoiceNumber }}</strong> comme payée ?</p>
      <form [formGroup]="paidForm">
        <mat-form-field class="full-width">
          <mat-label>Date de paiement</mat-label>
          <input matInput [matDatepicker]="picker" formControlName="paidDate">
          <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
          <mat-hint>Laisser vide pour utiliser la date du jour</mat-hint>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annuler</button>
      <button mat-raised-button color="primary" (click)="onConfirm()">
        Confirmer
      </button>
    </mat-dialog-actions>
  `
})
export class MarkAsPaidDialogComponent {
  paidForm: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<MarkAsPaidDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { invoiceNumber: string },
    private fb: FormBuilder
  ) {
    this.paidForm = this.fb.group({
      paidDate: [new Date()]
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    const paidDate = this.paidForm.value.paidDate;
    // Formater la date au format ISO si elle existe
    const formattedDate = paidDate ? paidDate.toISOString().split('T')[0] : undefined;
    this.dialogRef.close({ paidDate: formattedDate });
  }
}
```

## Styles CSS pour les badges de statut

```scss
.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 600;
  text-transform: uppercase;

  &.status-paid {
    background-color: #4caf50;
    color: white;
  }

  &.status-pending {
    background-color: #ff9800;
    color: white;
  }

  &.status-overdue {
    background-color: #f44336;
    color: white;
  }
}
```
