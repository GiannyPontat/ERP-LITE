import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { CatalogService } from '../../../core/services/catalog.service';
import { CatalogItem, CatalogCategory, CATALOG_CATEGORY_LABELS } from '../../../core/models/catalog.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-catalog-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatMenuModule,
    MatDividerModule,
    MatDialogModule
  ],
  templateUrl: './catalog-list.component.html',
  styleUrl: './catalog-list.component.scss'
})
export class CatalogListComponent implements OnInit {
  dataSource = new MatTableDataSource<CatalogItem>([]);
  items: CatalogItem[] = [];
  
  loading = false;
  totalElements = 0;
  pageSize = 20;
  pageIndex = 0;
  
  searchQuery = '';
  selectedCategory: CatalogCategory | null = null;
  viewMode: 'grid' | 'list' = 'grid';
  
  categories = Object.values(CatalogCategory);
  categoryLabels = CATALOG_CATEGORY_LABELS;
  
  private searchSubject = new Subject<string>();

  constructor(
    private catalogService: CatalogService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadItems();
    
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(() => {
      this.pageIndex = 0;
      this.loadItems();
    });
  }

  loadItems(): void {
    this.loading = true;
    
    this.catalogService.getAll(
      this.pageIndex,
      this.pageSize,
      this.searchQuery || undefined,
      this.selectedCategory || undefined
    ).subscribe({
      next: (response) => {
        this.items = response.content;
        this.dataSource.data = response.content;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading catalog items:', error);
        this.snackBar.open('Erreur lors du chargement du catalogue', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
      }
    });
  }

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchQuery = value;
    this.searchSubject.next(value);
  }

  filterByCategory(category: CatalogCategory | null): void {
    this.selectedCategory = category;
    this.pageIndex = 0;
    this.loadItems();
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadItems();
  }

  getActiveItems(): number {
    return this.items.filter(i => i.isActive).length;
  }

  getLowStockItems(): number {
    return this.items.filter(i => this.isLowStock(i)).length;
  }

  getUniqueCategories(): number {
    const categories = new Set(this.items.map(i => i.category));
    return categories.size;
  }

  getItemsByCategory(category: CatalogCategory): CatalogItem[] {
    return this.items.filter(i => i.category === category);
  }

  isLowStock(item: CatalogItem): boolean {
    if (item.stock === undefined || item.stock === null) return false;
    if (item.minStock === undefined || item.minStock === null) return false;
    return item.stock <= item.minStock;
  }

  getCategoryLabel(category: CatalogCategory): string {
    return this.categoryLabels[category] || category;
  }

  viewItem(item: CatalogItem): void {
    this.router.navigate(['/catalog', item.id]);
  }

  editItem(item: CatalogItem): void {
    this.router.navigate(['/catalog', item.id, 'edit']);
  }

  toggleActive(item: CatalogItem): void {
    if (!item.id) return;

    const newStatus = !item.isActive;
    this.catalogService.update(item.id, { isActive: newStatus }).subscribe({
      next: () => {
        item.isActive = newStatus;
        this.snackBar.open(
          item.isActive ? 'Article activé' : 'Article désactivé',
          'Fermer',
          { duration: 3000 }
        );
      },
      error: (error: any) => {
        console.error('Error toggling active status:', error);
        this.snackBar.open('Erreur lors de la modification', 'Fermer', {
          duration: 5000
        });
      }
    });
  }

  deleteItem(item: CatalogItem): void {
    if (!item.id) return;

    const dialogData: ConfirmDialogData = {
      title: 'Supprimer l\'article',
      message: `Êtes-vous sûr de vouloir supprimer l'article "${item.name}" ?`,
      confirmText: 'Supprimer',
      cancelText: 'Annuler'
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: dialogData,
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && item.id) {
        this.catalogService.delete(item.id).subscribe({
          next: () => {
            this.snackBar.open('Article supprimé', 'Fermer', { duration: 3000 });
            this.loadItems();
          },
          error: (error: any) => {
            console.error('Error deleting item:', error);
            this.snackBar.open('Erreur lors de la suppression', 'Fermer', {
              duration: 5000
            });
          }
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
