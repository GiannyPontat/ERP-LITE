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
import { InvoiceService } from '../../../core/services/invoice.service';
import { QuoteService } from '../../../core/services/quote.service';
import { ClientService } from '../../../core/services/client.service';
import { Invoice, InvoiceStatus } from '../../../core/models/invoice.model';
import { Quote } from '../../../core/models/quote.model';
import { Client } from '../../../core/models/client.model';
import { Page } from '../../../core/models/page.model';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../../shared/pipes/capitalize.pipe';

@Component({
  selector: 'app-invoice-form',
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
  templateUrl: './invoice-form.component.html',
  styleUrl: './invoice-form.component.scss'
})
export class InvoiceFormComponent implements OnInit {
  invoiceForm!: FormGroup;
  invoiceId?: number;
  quoteId?: number;
  isEditMode = false;
  loading = false;
  clients: Client[] = [];
  quotes: Quote[] = [];
  statuses = Object.values(InvoiceStatus);

  constructor(
    private fb: FormBuilder,
    private invoiceService: InvoiceService,
    private quoteService: QuoteService,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.invoiceId = this.route.snapshot.params['id'];
    this.quoteId = this.route.snapshot.queryParams['quoteId'];
    this.isEditMode = !!this.invoiceId;

    this.loadClients();
    if (this.quoteId) {
      this.loadQuote();
    }
    this.initForm();

    if (this.isEditMode) {
      this.loadInvoice();
    }
  }

  initForm(): void {
    this.invoiceForm = this.fb.group({
      clientId: [null, Validators.required],
      quoteId: [null],
      date: [new Date(), Validators.required],
      dueDate: [null],
      status: [InvoiceStatus.DRAFT, Validators.required],
      taxRate: [20, [Validators.required, Validators.min(0), Validators.max(100)]],
      notes: [''],
      termsAndConditions: [''],
      items: this.fb.array([])
    });

    if (!this.quoteId) {
      this.addItem();
    }
  }

  get items(): FormArray {
    return this.invoiceForm.get('items') as FormArray;
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

    this.quoteService.getById(this.quoteId).subscribe({
      next: (quote) => {
        this.invoiceForm.patchValue({
          clientId: quote.clientId,
          quoteId: quote.id,
          date: new Date(),
          taxRate: quote.taxRate || 20
        });

        // Clear items and add quote items
        while (this.items.length !== 0) {
          this.items.removeAt(0);
        }

        quote.items.forEach(item => {
          const itemGroup = this.fb.group({
            description: [item.description, Validators.required],
            quantity: [item.quantity, [Validators.required, Validators.min(0.01)]],
            unitPrice: [item.unitPrice, [Validators.required, Validators.min(0)]]
          });
          this.items.push(itemGroup);
        });
      }
    });
  }

  loadInvoice(): void {
    if (!this.invoiceId) return;

    this.loading = true;
    this.invoiceService.getById(this.invoiceId).subscribe({
      next: (invoice) => {
        while (this.items.length !== 0) {
          this.items.removeAt(0);
        }

        this.invoiceForm.patchValue({
          clientId: invoice.clientId,
          quoteId: invoice.quoteId,
          date: new Date(invoice.date),
          dueDate: invoice.dueDate ? new Date(invoice.dueDate) : null,
          status: invoice.status,
          taxRate: invoice.taxRate || 20,
          notes: invoice.notes || '',
          termsAndConditions: invoice.termsAndConditions || ''
        });

        invoice.items.forEach(item => {
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
        console.error('Error loading invoice:', error);
        this.snackBar.open('Erreur lors du chargement de la facture', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
        this.router.navigate(['/invoices']);
      }
    });
  }

  onSubmit(): void {
    if (this.invoiceForm.invalid) {
      this.markFormGroupTouched(this.invoiceForm);
      return;
    }

    this.loading = true;
    const formValue = this.invoiceForm.value;

    const invoice: Invoice = {
      clientId: formValue.clientId,
      quoteId: formValue.quoteId,
      date: formValue.date.toISOString().split('T')[0],
      dueDate: formValue.dueDate ? formValue.dueDate.toISOString().split('T')[0] : undefined,
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

    if (this.isEditMode && this.invoiceId) {
      invoice.id = this.invoiceId;
      this.invoiceService.update(this.invoiceId, invoice).subscribe({
        next: () => {
          this.snackBar.open('Facture mise à jour avec succès', 'Fermer', {
            duration: 3000
          });
          this.router.navigate(['/invoices', this.invoiceId]);
        },
        error: (error) => {
          console.error('Error updating invoice:', error);
          this.snackBar.open(error.error?.message || 'Erreur lors de la mise à jour', 'Fermer', {
            duration: 5000
          });
          this.loading = false;
        }
      });
    } else if (this.quoteId) {
      this.invoiceService.createFromQuote(this.quoteId, invoice).subscribe({
        next: (createdInvoice) => {
          this.snackBar.open('Facture créée depuis le devis avec succès', 'Fermer', {
            duration: 3000
          });
          this.router.navigate(['/invoices', createdInvoice.id]);
        },
        error: (error) => {
          console.error('Error creating invoice from quote:', error);
          this.snackBar.open(error.error?.message || 'Erreur lors de la création', 'Fermer', {
            duration: 5000
          });
          this.loading = false;
        }
      });
    } else {
      this.invoiceService.create(invoice).subscribe({
        next: (createdInvoice) => {
          this.snackBar.open('Facture créée avec succès', 'Fermer', {
            duration: 3000
          });
          this.router.navigate(['/invoices', createdInvoice.id]);
        },
        error: (error) => {
          console.error('Error creating invoice:', error);
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
    const taxRate = this.invoiceForm.get('taxRate')?.value || 0;
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
