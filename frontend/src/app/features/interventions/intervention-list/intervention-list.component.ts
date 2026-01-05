import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

// Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';

import { InterventionService } from '../../../core/services/intervention.service';
import {
  Intervention,
  InterventionStatus,
  InterventionType,
  INTERVENTION_STATUS_LABELS,
  INTERVENTION_STATUS_COLORS,
  INTERVENTION_TYPE_LABELS,
  INTERVENTION_TYPE_ICONS
} from '../../../core/models/intervention.model';

@Component({
  selector: 'app-intervention-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatMenuModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatBadgeModule,
    MatDividerModule
  ],
  templateUrl: './intervention-list.component.html',
  styleUrls: ['./intervention-list.component.scss']
})
export class InterventionListComponent implements OnInit {
  interventions: Intervention[] = [];
  filteredInterventions: Intervention[] = [];
  loading = true;
  
  selectedStatus: InterventionStatus | 'ALL' = 'ALL';
  selectedType: InterventionType | 'ALL' = 'ALL';
  searchQuery = '';
  viewMode: 'grid' | 'list' = 'grid';
  
  statusCounts: Record<InterventionStatus, number> = {} as Record<InterventionStatus, number>;
  
  readonly InterventionStatus = InterventionStatus;
  readonly InterventionType = InterventionType;
  readonly STATUS_LABELS = INTERVENTION_STATUS_LABELS;
  readonly STATUS_COLORS = INTERVENTION_STATUS_COLORS;
  readonly TYPE_LABELS = INTERVENTION_TYPE_LABELS;
  readonly TYPE_ICONS = INTERVENTION_TYPE_ICONS;
  
  readonly statusList = Object.values(InterventionStatus);
  readonly typeList = Object.values(InterventionType);

  constructor(private interventionService: InterventionService) {}

  ngOnInit(): void {
    this.loadInterventions();
    this.loadStatusCounts();
  }

  loadInterventions(): void {
    this.loading = true;
    this.interventionService.getAll().subscribe({
      next: (data) => {
        this.interventions = this.sortInterventions(data);
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadStatusCounts(): void {
    this.interventionService.getCountsByStatus().subscribe({
      next: (counts) => {
        this.statusCounts = counts;
      }
    });
  }

  /**
   * Tri : Urgent en premier, puis par date
   */
  sortInterventions(interventions: Intervention[]): Intervention[] {
    const statusPriority: Record<InterventionStatus, number> = {
      [InterventionStatus.URGENT]: 0,
      [InterventionStatus.IN_PROGRESS]: 1,
      [InterventionStatus.TO_INVOICE]: 2,
      [InterventionStatus.PLANNED]: 3,
      [InterventionStatus.COMPLETED]: 4
    };

    return [...interventions].sort((a, b) => {
      const priorityDiff = statusPriority[a.status] - statusPriority[b.status];
      if (priorityDiff !== 0) return priorityDiff;
      
      // Même priorité : tri par date (plus récent en premier)
      const dateA = a.scheduledDate || a.createdAt;
      const dateB = b.scheduledDate || b.createdAt;
      return new Date(dateB).getTime() - new Date(dateA).getTime();
    });
  }

  applyFilters(): void {
    let result = [...this.interventions];
    
    // Filtre par statut
    if (this.selectedStatus !== 'ALL') {
      result = result.filter(i => i.status === this.selectedStatus);
    }
    
    // Filtre par type
    if (this.selectedType !== 'ALL') {
      result = result.filter(i => i.type === this.selectedType);
    }
    
    // Recherche textuelle
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase().trim();
      result = result.filter(i =>
        i.clientName.toLowerCase().includes(query) ||
        i.title.toLowerCase().includes(query) ||
        i.reference.toLowerCase().includes(query) ||
        (i.clientAddress && i.clientAddress.toLowerCase().includes(query))
      );
    }
    
    this.filteredInterventions = result;
  }

  onStatusFilterChange(): void {
    this.applyFilters();
  }

  onTypeFilterChange(): void {
    this.applyFilters();
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  clearFilters(): void {
    this.selectedStatus = 'ALL';
    this.selectedType = 'ALL';
    this.searchQuery = '';
    this.applyFilters();
  }

  /**
   * Action principale selon le statut
   */
  getPrimaryAction(intervention: Intervention): { label: string; icon: string; action: string } {
    switch (intervention.status) {
      case InterventionStatus.URGENT:
        return { label: 'Démarrer', icon: 'play_arrow', action: 'start' };
      case InterventionStatus.PLANNED:
        return { label: 'Démarrer', icon: 'play_arrow', action: 'start' };
      case InterventionStatus.IN_PROGRESS:
        return { label: 'Terminer', icon: 'check', action: 'complete' };
      case InterventionStatus.COMPLETED:
        return { label: 'Facturer', icon: 'receipt', action: 'invoice' };
      case InterventionStatus.TO_INVOICE:
        return { label: 'Créer facture', icon: 'receipt_long', action: 'create_invoice' };
      default:
        return { label: 'Voir', icon: 'visibility', action: 'view' };
    }
  }

  onPrimaryAction(intervention: Intervention): void {
    const action = this.getPrimaryAction(intervention);
    
    switch (action.action) {
      case 'start':
        this.changeStatus(intervention, InterventionStatus.IN_PROGRESS);
        break;
      case 'complete':
        this.changeStatus(intervention, InterventionStatus.COMPLETED);
        break;
      case 'invoice':
      case 'create_invoice':
        this.changeStatus(intervention, InterventionStatus.TO_INVOICE);
        console.log('Créer facture pour intervention:', intervention.id);
        break;
      case 'view':
        console.log('Voir intervention:', intervention.id);
        break;
    }
  }

  changeStatus(intervention: Intervention, newStatus: InterventionStatus): void {
    const updatedIntervention = { ...intervention, status: newStatus };
    this.interventionService.update(intervention.id, updatedIntervention).subscribe({
      next: () => {
        this.loadInterventions();
        this.loadStatusCounts();
      }
    });
  }

  viewIntervention(intervention: Intervention): void {
    console.log('View intervention:', intervention.id);
  }

  editIntervention(intervention: Intervention): void {
    console.log('Edit intervention:', intervention.id);
  }

  deleteIntervention(intervention: Intervention): void {
    if (confirm(`Supprimer l'intervention "${intervention.title}" ?`)) {
      this.interventionService.delete(intervention.id).subscribe({
        next: () => {
          this.loadInterventions();
          this.loadStatusCounts();
        }
      });
    }
  }

  createInvoice(intervention: Intervention): void {
    console.log('Create invoice for intervention:', intervention.id);
  }

  /**
   * Formatage date relative
   */
  getRelativeDate(dateStr: string | undefined): string {
    if (!dateStr) return '';
    
    const date = new Date(dateStr);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    
    const targetDate = new Date(date);
    targetDate.setHours(0, 0, 0, 0);
    
    if (targetDate.getTime() === today.getTime()) {
      return "Aujourd'hui";
    } else if (targetDate.getTime() === tomorrow.getTime()) {
      return 'Demain';
    } else if (targetDate.getTime() === yesterday.getTime()) {
      return 'Hier';
    } else {
      return date.toLocaleDateString('fr-FR', { 
        weekday: 'short', 
        day: 'numeric', 
        month: 'short' 
      });
    }
  }

  /**
   * Formatage durée
   */
  formatDuration(minutes: number | undefined): string {
    if (!minutes) return '';
    if (minutes < 60) return `${minutes} min`;
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return mins > 0 ? `${hours}h${mins}` : `${hours}h`;
  }

  /**
   * Formatage montant
   */
  formatAmount(amount: number | undefined): string {
    if (!amount) return '';
    return new Intl.NumberFormat('fr-FR', { 
      style: 'currency', 
      currency: 'EUR' 
    }).format(amount);
  }

  /**
   * Style du badge statut
   */
  getStatusStyle(status: InterventionStatus): object {
    const colors = this.STATUS_COLORS[status];
    return {
      'background-color': colors.bg,
      'color': colors.text
    };
  }

  trackByIntervention(index: number, item: Intervention): number {
    return item.id;
  }
}

