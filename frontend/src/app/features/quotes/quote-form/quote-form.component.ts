import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { QuoteService } from '../../../core/services/quote.service';
import { ClientService } from '../../../core/services/client.service';
import { Quote, QuoteStatus } from '../../../core/models/quote.model';
import { Client } from '../../../core/models/client.model';
import { Page } from '../../../core/models/page.model';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../../shared/pipes/capitalize.pipe';

@Component({
  selector: 'app-quote-form',
  standalone: true,
  imports: [
    CommonModule,
    DecimalPipe,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    TranslateModule,
    CapitalizePipe
  ],
  templateUrl: './quote-form.component.html',
  styleUrl: './quote-form.component.scss'
})
export class QuoteFormComponent implements OnInit {
  quoteForm!: FormGroup;
  quoteId?: number;
  isEditMode = false;
  loading = false;
  clients: Client[] = [];
  statuses = Object.values(QuoteStatus);

  constructor(
    private fb: FormBuilder,
    private quoteService: QuoteService,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.quoteId = this.route.snapshot.params['id'];
    this.isEditMode = !!this.quoteId;

    this.loadClients();
    this.initForm();

    if (this.isEditMode) {
      this.loadQuote();
    }
  }

  initForm(): void {
    this.quoteForm = this.fb.group({
      clientId: [null, Validators.required],
      date: [new Date(), Validators.required],
      validUntil: [null],
      status: [QuoteStatus.DRAFT, Validators.required],
      taxRate: [20, [Validators.required, Validators.min(0), Validators.max(100)]],
      notes: [''],
      termsAndConditions: [''],
      items: this.fb.array([])
    });

    // Ajouter une ligne par défaut
    this.addItem();
  }

  get items(): FormArray {
    return this.quoteForm.get('items') as FormArray;
  }

  addItem(): void {
    const itemGroup = this.fb.group({
      description: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(0.01)]],
      unitPrice: [0, [Validators.required, Validators.min(0)]]
    });
    this.items.push(itemGroup);
  }

  removeItem(index: number): void {
    if (this.items.length > 1) {
      this.items.removeAt(index);
    }
  }

  loadClients(): void {
    this.clientService.getAll(0, 1000).subscribe({
      next: (page: Page<Client>) => {
        this.clients = page.content;
      }
    });
  }

  loadQuote(): void {
    if (!this.quoteId) return;

    this.loading = true;
    this.quoteService.getById(this.quoteId).subscribe({
      next: (quote) => {
        // Clear items array
        while (this.items.length !== 0) {
          this.items.removeAt(0);
        }

        // Patch form values
        this.quoteForm.patchValue({
          clientId: quote.clientId,
          date: new Date(quote.date),
          validUntil: quote.validUntil ? new Date(quote.validUntil) : null,
          status: quote.status,
          taxRate: quote.taxRate || 20,
          notes: quote.notes || '',
          termsAndConditions: quote.termsAndConditions || ''
        });

        // Add items
        quote.items.forEach(item => {
          const itemGroup = this.fb.group({
            description: [item.description, Validators.required],
            quantity: [item.quantity, [Validators.required, Validators.min(0.01)]],
            unitPrice: [item.unitPrice, [Validators.required, Validators.min(0)]]
          });
          this.items.push(itemGroup);
        });

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

  onSubmit(): void {
    if (this.quoteForm.invalid) {
      this.markFormGroupTouched(this.quoteForm);
      return;
    }

    this.loading = true;
    const formValue = this.quoteForm.value;

    const quote: Quote = {
      clientId: formValue.clientId,
      date: formValue.date.toISOString().split('T')[0],
      validUntil: formValue.validUntil ? formValue.validUntil.toISOString().split('T')[0] : undefined,
      status: formValue.status,
      taxRate: formValue.taxRate,
      notes: formValue.notes,
      termsAndConditions: formValue.termsAndConditions,
      items: formValue.items.map((item: any) => ({
        description: item.description,
        quantity: item.quantity,
        unitPrice: item.unitPrice
      }))
    };

    if (this.isEditMode && this.quoteId) {
      quote.id = this.quoteId;
      this.quoteService.update(this.quoteId, quote).subscribe({
        next: () => {
          this.snackBar.open('Devis mis à jour avec succès', 'Fermer', {
            duration: 3000
          });
          this.router.navigate(['/quotes', this.quoteId]);
        },
        error: (error) => {
          console.error('Error updating quote:', error);
          this.snackBar.open(error.error?.message || 'Erreur lors de la mise à jour', 'Fermer', {
            duration: 5000
          });
          this.loading = false;
        }
      });
    } else {
      this.quoteService.create(quote).subscribe({
        next: (createdQuote) => {
          this.snackBar.open('Devis créé avec succès', 'Fermer', {
            duration: 3000
          });
          this.router.navigate(['/quotes', createdQuote.id]);
        },
        error: (error) => {
          console.error('Error creating quote:', error);
          this.snackBar.open(error.error?.message || 'Erreur lors de la création', 'Fermer', {
            duration: 5000
          });
          this.loading = false;
        }
      });
    }
  }

  calculateItemTotal(index: number): number {
    const item = this.items.at(index).value;
    return (item.quantity || 0) * (item.unitPrice || 0);
  }

  calculateSubtotal(): number {
    return this.items.controls.reduce((sum, control) => {
      const item = control.value;
      return sum + ((item.quantity || 0) * (item.unitPrice || 0));
    }, 0);
  }

  calculateTaxAmount(): number {
    const subtotal = this.calculateSubtotal();
    const taxRate = this.quoteForm.get('taxRate')?.value || 0;
    return subtotal * (taxRate / 100);
  }

  calculateTotal(): number {
    return this.calculateSubtotal() + this.calculateTaxAmount();
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      } else if (control instanceof FormArray) {
        control.controls.forEach(arrayControl => {
          if (arrayControl instanceof FormGroup) {
            this.markFormGroupTouched(arrayControl);
          }
        });
      }
    });
  }
}
