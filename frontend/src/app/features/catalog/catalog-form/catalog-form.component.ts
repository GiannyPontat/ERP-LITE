import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { TranslateModule } from '@ngx-translate/core';
import { CatalogService } from '../../../core/services/catalog.service';
import { CatalogItem, CatalogCategory, CatalogUnit, CATALOG_CATEGORY_LABELS, CATALOG_UNIT_LABELS } from '../../../core/models/catalog.model';

@Component({
  selector: 'app-catalog-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDividerModule,
    MatAutocompleteModule,
    TranslateModule
  ],
  templateUrl: './catalog-form.component.html',
  styleUrl: './catalog-form.component.scss'
})
export class CatalogFormComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  saving = false;
  isEditMode = false;
  itemId?: number;
  
  categories = Object.values(CatalogCategory);
  categoryLabels = CATALOG_CATEGORY_LABELS;
  units = Object.values(CatalogUnit);
  unitLabels = CATALOG_UNIT_LABELS;

  constructor(
    private fb: FormBuilder,
    private catalogService: CatalogService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadMetadata();
    
    const id = this.route.snapshot.params['id'];
    if (id && id !== 'new') {
      this.isEditMode = true;
      this.itemId = +id;
      this.loadItem(this.itemId);
    }
  }

  private initForm(): void {
    this.form = this.fb.group({
      reference: ['', [Validators.required, Validators.maxLength(50)]],
      name: ['', [Validators.required, Validators.maxLength(500)]],
      description: ['', [Validators.maxLength(5000)]],
      category: [null, [Validators.required]],
      unit: [CatalogUnit.PIECE, [Validators.required]],
      unitPrice: [null, [Validators.required, Validators.min(0)]],
      taxRate: [20, [Validators.min(0), Validators.max(100)]],
      supplier: ['', [Validators.maxLength(255)]],
      stock: [0, [Validators.min(0)]],
      minStock: [0, [Validators.min(0)]],
      isActive: [true],
      notes: ['', [Validators.maxLength(5000)]]
    });
  }

  private loadMetadata(): void {
    // No metadata to load for now
  }

  private loadItem(id: number): void {
    this.loading = true;
    this.catalogService.getById(id).subscribe({
      next: (item) => {
        if (item) {
          this.form.patchValue({
            reference: item.reference,
            name: item.name,
            description: item.description,
            category: item.category,
            unit: item.unit,
            unitPrice: item.unitPrice,
            taxRate: item.taxRate ?? 20,
            supplier: item.supplier,
            stock: item.stock ?? 0,
            minStock: item.minStock ?? 0,
            isActive: item.isActive ?? true,
            notes: item.notes
          });
        }
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading item:', error);
        this.snackBar.open('Erreur lors du chargement de l\'article', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
        this.router.navigate(['/catalog']);
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    const itemData: CatalogItem = this.form.value;

    const request = this.isEditMode && this.itemId
      ? this.catalogService.update(this.itemId, itemData)
      : this.catalogService.create(itemData);

    request.subscribe({
      next: (result) => {
        this.snackBar.open(
          this.isEditMode ? 'Article modifié avec succès' : 'Article créé avec succès',
          'Fermer',
          { duration: 3000 }
        );
        this.router.navigate(['/catalog', result.id]);
      },
      error: (error) => {
        console.error('Error saving item:', error);
        let message = 'Erreur lors de l\'enregistrement';
        if (error.error?.message) {
          message = error.error.message;
        } else if (error.status === 409) {
          message = 'Cette référence existe déjà';
        }
        this.snackBar.open(message, 'Fermer', { duration: 5000 });
        this.saving = false;
      }
    });
  }

  getCategoryLabel(category: CatalogCategory): string {
    return this.categoryLabels[category] || category;
  }

  hasError(field: string, error: string): boolean {
    const control = this.form.get(field);
    return control ? control.hasError(error) && control.touched : false;
  }
}

