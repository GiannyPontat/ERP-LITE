# Implémentation QuoteDetailComponent

## Fonctionnalités à ajouter

Ce fichier montre comment implémenter les nouvelles fonctionnalités dans le composant QuoteDetailComponent.

## Code TypeScript

```typescript
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { QuoteService } from '../../core/services/quote.service';
import { Quote } from '../../core/models/quote.model';

@Component({
  selector: 'app-quote-detail',
  templateUrl: './quote-detail.component.html',
  styleUrls: ['./quote-detail.component.scss']
})
export class QuoteDetailComponent implements OnInit {
  quote?: Quote;
  loading = false;
  sendingEmail = false;
  converting = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quoteService: QuoteService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.loadQuote(id);
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
      }
    });
  }

  /**
   * Télécharge le PDF du devis
   */
  downloadPdf(): void {
    if (!this.quote) return;

    this.quoteService.generatePdf(this.quote.id).subscribe({
      next: (blob) => {
        // Créer un lien de téléchargement
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `devis-${this.quote!.quoteNumber}.pdf`;
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
   * Ouvre un dialog pour envoyer le devis par email
   */
  openSendEmailDialog(): void {
    if (!this.quote) return;

    // Utiliser MatDialog pour ouvrir un dialog avec un input email
    const dialogRef = this.dialog.open(SendEmailDialogComponent, {
      width: '400px',
      data: {
        title: 'Envoyer le devis par email',
        recipientEmail: this.quote.client?.email || ''
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.email) {
        this.sendEmail(result.email);
      }
    });
  }

  /**
   * Envoie le devis par email
   */
  sendEmail(recipientEmail: string): void {
    if (!this.quote) return;

    this.sendingEmail = true;
    this.quoteService.sendQuoteByEmail(this.quote.id, recipientEmail).subscribe({
      next: (response) => {
        this.snackBar.open('Devis envoyé avec succès', 'Fermer', {
          duration: 3000
        });
        this.sendingEmail = false;
        // Recharger le devis pour mettre à jour le statut
        this.loadQuote(this.quote!.id);
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
   * Convertit le devis en facture
   */
  convertToInvoice(): void {
    if (!this.quote) return;

    // Vérifier que le devis est accepté
    if (this.quote.status !== 'ACCEPTED') {
      this.snackBar.open('Seuls les devis acceptés peuvent être convertis en facture', 'Fermer', {
        duration: 5000
      });
      return;
    }

    // Demander confirmation
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Convertir en facture',
        message: `Êtes-vous sûr de vouloir convertir le devis ${this.quote.quoteNumber} en facture ?`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.performConversion();
      }
    });
  }

  private performConversion(): void {
    if (!this.quote) return;

    this.converting = true;
    this.quoteService.convertToInvoice(this.quote.id).subscribe({
      next: (invoice) => {
        this.snackBar.open('Devis converti en facture avec succès', 'Fermer', {
          duration: 3000
        });
        this.converting = false;
        // Rediriger vers la facture créée
        this.router.navigate(['/invoices', invoice.id]);
      },
      error: (error) => {
        console.error('Error converting quote:', error);
        this.snackBar.open('Erreur lors de la conversion', 'Fermer', {
          duration: 5000
        });
        this.converting = false;
      }
    });
  }
}
```

## Template HTML - Boutons à ajouter

```html
<!-- Dans le header du devis -->
<div class="quote-actions">
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

  <!-- Bouton Convertir en facture (visible seulement si status = ACCEPTED) -->
  <button mat-raised-button color="primary"
          (click)="convertToInvoice()"
          [disabled]="loading || converting || quote?.status !== 'ACCEPTED'"
          *ngIf="quote?.status === 'ACCEPTED'">
    <mat-icon>receipt</mat-icon>
    {{ converting ? 'Conversion...' : 'Convertir en facture' }}
  </button>
</div>
```

## Dialog Component pour l'envoi d'email

```typescript
// send-email-dialog.component.ts
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-send-email-dialog',
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <form [formGroup]="emailForm">
        <mat-form-field class="full-width">
          <mat-label>Email destinataire</mat-label>
          <input matInput type="email" formControlName="email" required>
          <mat-error *ngIf="emailForm.get('email')?.hasError('required')">
            L'email est requis
          </mat-error>
          <mat-error *ngIf="emailForm.get('email')?.hasError('email')">
            Email invalide
          </mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annuler</button>
      <button mat-raised-button color="primary"
              (click)="onConfirm()"
              [disabled]="emailForm.invalid">
        Envoyer
      </button>
    </mat-dialog-actions>
  `
})
export class SendEmailDialogComponent {
  emailForm: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<SendEmailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { title: string, recipientEmail: string },
    private fb: FormBuilder
  ) {
    this.emailForm = this.fb.group({
      email: [data.recipientEmail, [Validators.required, Validators.email]]
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    if (this.emailForm.valid) {
      this.dialogRef.close({ email: this.emailForm.value.email });
    }
  }
}
```
