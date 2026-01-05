import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { TranslateModule } from '@ngx-translate/core';
import { CatalogService } from '../../../core/services/catalog.service';
import { CatalogItem, CatalogCategory, CATALOG_CATEGORY_LABELS } from '../../../core/models/catalog.model';

@Component({
  selector: 'app-catalog-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDividerModule,
    MatChipsModule,
    MatMenuModule,
    TranslateModule
  ],
  templateUrl: './catalog-detail.component.html',
  styleUrl: './catalog-detail.component.scss'
})
export class CatalogDetailComponent implements OnInit {
  item?: CatalogItem;
  loading = false;
  categoryLabels = CATALOG_CATEGORY_LABELS;

  constructor(
    private catalogService: CatalogService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.loadItem(+id);
    }
  }

  loadItem(id: number): void {
    this.loading = true;
    this.catalogService.getById(id).subscribe({
      next: (item) => {
        this.item = item;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading item:', error);
        this.snackBar.open('Erreur lors du chargement de l\'article', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
        this.router.navigate(['/catalog']);
      }
    });
  }

  toggleActive(): void {
    if (!this.item?.id) return;

    this.catalogService.update(this.item.id, { isActive: !this.item.isActive }).subscribe({
      next: () => {
        if (this.item) {
          this.item.isActive = !this.item.isActive;
          this.snackBar.open(
            this.item.isActive ? 'Article activé' : 'Article désactivé',
            'Fermer',
            { duration: 3000 }
          );
        }
      },
      error: (error: any) => {
        console.error('Error toggling active status:', error);
        this.snackBar.open('Erreur lors de la modification', 'Fermer', {
          duration: 5000
        });
      }
    });
  }

  deleteItem(): void {
    if (!this.item?.id) return;

    if (confirm(`Supprimer l'article "${this.item.name}" ?`)) {
      this.catalogService.delete(this.item.id).subscribe({
        next: () => {
          this.snackBar.open('Article supprimé', 'Fermer', { duration: 3000 });
          this.router.navigate(['/catalog']);
        },
        error: (error: any) => {
          console.error('Error deleting item:', error);
          this.snackBar.open('Erreur lors de la suppression', 'Fermer', {
            duration: 5000
          });
        }
      });
    }
  }

  getCategoryLabel(category?: CatalogCategory): string {
    if (!category) return '-';
    return this.categoryLabels[category] || category;
  }

  formatCurrency(amount?: number): string {
    if (amount === undefined || amount === null) return '-';
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }

  formatPercent(value?: number): string {
    if (value === undefined || value === null) return '-';
    return `${value.toFixed(1)}%`;
  }

  formatDate(date?: string): string {
    if (!date) return '-';
    return new Intl.DateTimeFormat('fr-FR', {
      dateStyle: 'long',
      timeStyle: 'short'
    }).format(new Date(date));
  }
}

