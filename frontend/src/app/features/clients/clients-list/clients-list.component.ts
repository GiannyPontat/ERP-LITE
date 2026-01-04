import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ClientService } from '../../../core/services/client.service';
import { Client } from '../../../core/models/client.model';
import { Page } from '../../../core/models/page.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../../shared/pipes/capitalize.pipe';

@Component({
  selector: 'app-clients-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatToolbarModule,
    MatTooltipModule,
    MatSnackBarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    TranslateModule,
    CapitalizePipe
  ],
  templateUrl: './clients-list.component.html',
  styleUrl: './clients-list.component.scss'
})
export class ClientsListComponent implements OnInit {
  displayedColumns: string[] = ['companyName', 'contactName', 'email', 'phone', 'city', 'actions'];
  dataSource = new MatTableDataSource<Client>([]);
  
  searchControl = new FormControl('');
  loading = false;
  totalElements = 0;
  pageSize = 20;
  pageIndex = 0;
  searchTerm = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private clientService: ClientService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadClients();

    // Recherche avec debounce
    this.searchControl.valueChanges
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe(search => {
        this.searchTerm = search || '';
        this.pageIndex = 0;
        this.loadClients();
      });
  }

  loadClients(): void {
    this.loading = true;
    this.clientService.getAll(this.pageIndex, this.pageSize, this.searchTerm).subscribe({
      next: (page: Page<Client>) => {
        this.dataSource.data = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading clients:', error);
        this.snackBar.open('Erreur lors du chargement des clients', 'Fermer', {
          duration: 5000
        });
        this.loading = false;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadClients();
  }

  editClient(client: Client): void {
    this.router.navigate(['/clients', client.id, 'edit']);
  }

  viewClient(client: Client): void {
    this.router.navigate(['/clients', client.id]);
  }

  deleteClient(client: Client): void {
    const dialogData: ConfirmDialogData = {
      title: 'Supprimer le client',
      message: `Êtes-vous sûr de vouloir supprimer le client "${client.companyName || client.entreprise || client.nom}" ?`,
      confirmText: 'Supprimer',
      cancelText: 'Annuler'
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: dialogData,
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.clientService.delete(client.id).subscribe({
          next: () => {
            this.snackBar.open('Client supprimé avec succès', 'Fermer', {
              duration: 3000
            });
            this.loadClients();
          },
          error: (error) => {
            console.error('Error deleting client:', error);
            this.snackBar.open('Erreur lors de la suppression du client', 'Fermer', {
              duration: 5000
            });
          }
        });
      }
    });
  }

  getContactName(client: Client): string {
    if (client.contactFirstName && client.contactLastName) {
      return `${client.contactFirstName} ${client.contactLastName}`;
    }
    return client.nom || '-';
  }

  getCompanyName(client: Client): string {
    return client.companyName || client.entreprise || '-';
  }
}
