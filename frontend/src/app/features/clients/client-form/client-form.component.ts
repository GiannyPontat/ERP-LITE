import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ClientService } from '../../../core/services/client.service';
import { CreateClientDto, UpdateClientDto } from '../../../core/models/client.model';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../../shared/pipes/capitalize.pipe';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    TranslateModule,
    CapitalizePipe
  ],
  templateUrl: './client-form.component.html',
  styleUrl: './client-form.component.scss'
})
export class ClientFormComponent implements OnInit {
  clientForm!: FormGroup;
  clientId?: number;
  isEditMode = false;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.clientId = this.route.snapshot.params['id'];
    this.isEditMode = !!this.clientId;

    this.initForm();

    if (this.isEditMode) {
      this.loadClient();
    }
  }

  initForm(): void {
    this.clientForm = this.fb.group({
      companyName: ['', [Validators.required, Validators.maxLength(255)]],
      siret: ['', [Validators.pattern(/^\d{14}$/)]],
      contactFirstName: ['', [Validators.maxLength(100)]],
      contactLastName: ['', [Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      phone: ['', [Validators.pattern(/^[0-9+\s\-()]+$/)]],
      address: ['', [Validators.maxLength(255)]],
      city: ['', [Validators.maxLength(100)]],
      postalCode: ['', [Validators.pattern(/^\d{5}$/)]],
      paymentTerms: [30, [Validators.min(0)]],
      notes: ['']
    });
  }

  loadClient(): void {
    if (!this.clientId) return;

    this.loading = true;
    this.clientService.getById(this.clientId).subscribe({
      next: (client) => {
        this.clientForm.patchValue({
          companyName: client.companyName || '',
          siret: client.siret || '',
          contactFirstName: client.contactFirstName || '',
          contactLastName: client.contactLastName || '',
          email: client.email || '',
          phone: client.phone || client.telephone || '',
          address: client.address || client.adresse || '',
          city: client.city || '',
          postalCode: client.postalCode || '',
          paymentTerms: client.paymentTerms || 30,
          notes: client.notes || ''
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading client:', error);
        this.snackBar.open('Erreur lors du chargement du client', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
        this.router.navigate(['/clients']);
      }
    });
  }

  onSubmit(): void {
    if (this.clientForm.invalid) {
      this.markFormGroupTouched(this.clientForm);
      return;
    }

    this.loading = true;
    const formValue = this.clientForm.value;

    if (this.isEditMode && this.clientId) {
      const updateDto: UpdateClientDto = formValue;
      this.clientService.update(this.clientId, updateDto).subscribe({
        next: () => {
          this.snackBar.open('Client mis à jour avec succès', 'Fermer', {
            duration: 3000
          });
          this.router.navigate(['/clients', this.clientId]);
        },
        error: (error) => {
          console.error('Error updating client:', error);
          this.snackBar.open(error.error?.message || 'Erreur lors de la mise à jour', 'Fermer', {
            duration: 5000
          });
          this.loading = false;
        }
      });
    } else {
      const createDto: CreateClientDto = formValue;
      this.clientService.create(createDto).subscribe({
        next: (client) => {
          this.snackBar.open('Client créé avec succès', 'Fermer', {
            duration: 3000
          });
          this.router.navigate(['/clients', client.id]);
        },
        error: (error) => {
          console.error('Error creating client:', error);
          this.snackBar.open(error.error?.message || 'Erreur lors de la création', 'Fermer', {
            duration: 5000
          });
          this.loading = false;
        }
      });
    }
  }

  getErrorMessage(controlName: string): string {
    const control = this.clientForm.get(controlName);
    if (control?.hasError('required')) {
      return 'Ce champ est requis';
    }
    if (control?.hasError('email')) {
      return 'Format d\'email invalide';
    }
    if (control?.hasError('pattern')) {
      if (controlName === 'siret') {
        return 'Le SIRET doit contenir 14 chiffres';
      }
      if (controlName === 'postalCode') {
        return 'Le code postal doit contenir 5 chiffres';
      }
      return 'Format invalide';
    }
    if (control?.hasError('maxlength')) {
      return `Maximum ${control.errors?.['maxlength'].requiredLength} caractères`;
    }
    return '';
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }
}
