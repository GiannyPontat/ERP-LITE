import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { ClientService } from '../../../core/services/client.service';
import { Client } from '../../../core/models/client.model';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../../shared/pipes/capitalize.pipe';

@Component({
  selector: 'app-client-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    TranslateModule,
    CapitalizePipe
  ],
  templateUrl: './client-detail.component.html',
  styleUrl: './client-detail.component.scss'
})
export class ClientDetailComponent implements OnInit {
  client?: Client;
  loading = false;

  constructor(
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.loadClient(+id);
    }
  }

  loadClient(id: number): void {
    this.loading = true;
    this.clientService.getById(id).subscribe({
      next: (client) => {
        this.client = client;
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

  getCompanyName(): string {
    return this.client?.companyName || this.client?.entreprise || '-';
  }

  getContactName(): string {
    if (this.client?.contactFirstName && this.client?.contactLastName) {
      return `${this.client.contactFirstName} ${this.client.contactLastName}`;
    }
    return this.client?.nom || '-';
  }
}
