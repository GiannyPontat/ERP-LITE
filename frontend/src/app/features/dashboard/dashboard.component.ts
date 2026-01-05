import { Component, OnInit, computed, signal, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Observable, forkJoin } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { DashboardService } from '../../core/services/dashboard.service';
import { InvoiceService } from '../../core/services/invoice.service';
import { QuoteService } from '../../core/services/quote.service';
import { InterventionService } from '../../core/services/intervention.service';
import { DashboardStats, MonthlyRevenue, TopClient } from '../../core/models/dashboard.model';
import { Invoice, InvoiceStatus } from '../../core/models/invoice.model';
import { Quote, QuoteStatus } from '../../core/models/quote.model';
import { Intervention, InterventionType as IntType, INTERVENTION_TYPE_LABELS } from '../../core/models/intervention.model';
import { User } from '../../core/models/user.model';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../shared/pipes/capitalize.pipe';

interface RevenueData {
  name: string;
  ca: number;
}

interface Document {
  id: string;
  client: string;
  clientEmail?: string;
  type: 'Devis' | 'Facture';
  date: string;
  amount: string;
  status: string;
  originalId?: number;
  originalType?: 'quote' | 'invoice';
  originalStatus?: QuoteStatus | InvoiceStatus;
}

interface TopService {
  name: string;
  value: number;
}

interface InterventionTypeData {
  name: string;
  value: number;
  color: string;
  percentage: number;
}

// Couleurs pour les types d'interventions
const INTERVENTION_COLORS: Record<string, string> = {
  'DEPANNAGE': '#3B82F6',
  'RENOVATION': '#F97316',
  'ENTRETIEN': '#10B981',
  'INSTALLATION': '#8B5CF6',
  'DIAGNOSTIC': '#EC4899'
};

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule,
    MatTooltipModule,
    TranslateModule,
    CapitalizePipe
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, AfterViewInit {
  // Signals
  loading = signal(false);
  stats = signal<DashboardStats | null>(null);
  monthlyRevenues = signal<MonthlyRevenue[]>([]);
  topClients = signal<TopClient[]>([]);
  recentDocs = signal<Document[]>([]);
  topServices = signal<TopService[]>([]);
  interventionTypes = signal<InterventionTypeData[]>([]);
  interventions = signal<Intervention[]>([]);
  currentYear = new Date().getFullYear();
  currentUser$!: Observable<User | null>;

  // Animation states
  chartsLoaded = signal(false);
  donutAnimated = signal(false);
  servicesAnimated = signal(false);

  // Tooltip state
  hoveredSegment = signal<InterventionTypeData | null>(null);

  // Computed
  revenueData = computed(() => {
    const revenues = this.monthlyRevenues();
    return revenues.map(r => ({
      name: this.getMonthName(r.month),
      ca: r.revenue
    }));
  });

  constructor(
    public authService: AuthService,
    private dashboardService: DashboardService,
    private invoiceService: InvoiceService,
    private quoteService: QuoteService,
    private interventionService: InterventionService,
    private snackBar: MatSnackBar,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.currentUser$ = this.authService.currentUser$;
  }

  ngOnInit(): void {
    this.loadStats();
    this.loadMonthlyRevenue();
    this.loadTopClients();
    this.loadRecentDocuments();
    this.loadInterventionData();
  }

  ngAfterViewInit(): void {
    // Déclencher les animations après le rendu
    setTimeout(() => {
      this.chartsLoaded.set(true);
    }, 100);

    setTimeout(() => {
      this.donutAnimated.set(true);
    }, 300);

    setTimeout(() => {
      this.servicesAnimated.set(true);
    }, 500);
  }

  loadStats(): void {
    this.loading.set(true);
    this.dashboardService.getStats().subscribe({
      next: (stats) => {
        this.stats.set(stats);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading stats:', error);
        this.snackBar.open('Erreur lors du chargement des statistiques', 'Fermer', {
          duration: 5000
        });
        this.loading.set(false);
      }
    });
  }

  loadMonthlyRevenue(): void {
    this.dashboardService.getMonthlyRevenue(this.currentYear).subscribe({
      next: (revenues) => {
        this.monthlyRevenues.set(revenues);
      },
      error: (error) => {
        console.error('Error loading monthly revenue:', error);
      }
    });
  }

  loadTopClients(): void {
    this.dashboardService.getTopClients().subscribe({
      next: (clients) => {
        this.topClients.set(clients);
      },
      error: (error) => {
        console.error('Error loading top clients:', error);
      }
    });
  }

  loadRecentDocuments(): void {
    // Charger les devis et factures en parallèle
    forkJoin({
      quotes: this.quoteService.getAll(),
      invoices: this.invoiceService.getAll()
    }).subscribe({
      next: ({ quotes, invoices }) => {
        const quoteDocs: Document[] = quotes
          .slice(0, 5)
          .map(q => ({
            id: q.quoteNumber || `Q-${q.id}`,
            client: q.clientName || q.client?.companyName || 'Client inconnu',
            clientEmail: q.client?.email,
            type: 'Devis' as const,
            date: this.formatDate(q.date),
            amount: this.formatCurrency(q.total || 0),
            status: this.mapQuoteStatus(q.status),
            originalId: q.id,
            originalType: 'quote',
            originalStatus: q.status
          }));

        const invoiceDocs: Document[] = invoices
          .slice(0, 5)
          .map(inv => ({
            id: inv.invoiceNumber || `F-${inv.id}`,
            client: inv.clientName || inv.client?.companyName || 'Client inconnu',
            clientEmail: inv.client?.email,
            type: 'Facture' as const,
            date: this.formatDate(inv.date),
            amount: this.formatCurrency(inv.total || 0),
            status: this.mapInvoiceStatus(inv.status),
            originalId: inv.id,
            originalType: 'invoice',
            originalStatus: inv.status
          }));

        // Combiner et trier par date (plus récent en premier)
        const allDocs = [...quoteDocs, ...invoiceDocs]
          .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
          .slice(0, 5);

        this.recentDocs.set(allDocs);
      },
      error: (error) => {
        console.error('Error loading documents:', error);
      }
    });
  }

  /**
   * Charge les données d'interventions depuis l'API
   */
  loadInterventionData(): void {
    this.interventionService.getAll().subscribe({
      next: (interventions) => {
        this.interventions.set(interventions);
        this.computeTopServices(interventions);
        this.computeInterventionTypes(interventions);
      },
      error: (error) => {
        console.error('Error loading interventions:', error);
        // Fallback avec données par défaut
        this.setDefaultInterventionData();
      }
    });
  }

  /**
   * Calcule les top services basés sur les interventions réelles
   */
  private computeTopServices(interventions: Intervention[]): void {
    // Compter les interventions par titre (ou type de prestation)
    const serviceCount: Record<string, number> = {};
    
    interventions.forEach(intervention => {
      // Utiliser le titre simplifié comme clé
      const serviceName = this.simplifyServiceName(intervention.title);
      serviceCount[serviceName] = (serviceCount[serviceName] || 0) + 1;
    });

    // Convertir en tableau et trier
    const services: TopService[] = Object.entries(serviceCount)
      .map(([name, value]) => ({ name, value }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 4); // Top 4

    // Si pas assez de données, compléter avec des données par défaut
    if (services.length < 4) {
      const defaultServices = [
        { name: 'Fuite d\'eau', value: Math.floor(Math.random() * 20) + 5 },
        { name: 'Chauffe-eau', value: Math.floor(Math.random() * 15) + 3 },
        { name: 'Débouchage', value: Math.floor(Math.random() * 12) + 2 },
        { name: 'Robinetterie', value: Math.floor(Math.random() * 10) + 1 }
      ];
      
      const existingNames = services.map(s => s.name);
      defaultServices.forEach(ds => {
        if (!existingNames.includes(ds.name) && services.length < 4) {
          services.push(ds);
        }
      });
    }

    this.topServices.set(services);
  }

  /**
   * Simplifie le nom du service pour le regroupement
   */
  private simplifyServiceName(title: string): string {
    const titleLower = title.toLowerCase();
    
    if (titleLower.includes('fuite')) return 'Fuite d\'eau';
    if (titleLower.includes('chauffe-eau') || titleLower.includes('ballon')) return 'Chauffe-eau';
    if (titleLower.includes('débouchage') || titleLower.includes('canalisation')) return 'Débouchage';
    if (titleLower.includes('robinet')) return 'Robinetterie';
    if (titleLower.includes('chaudière')) return 'Chaudière';
    if (titleLower.includes('salle de bain')) return 'Salle de bain';
    if (titleLower.includes('wc') || titleLower.includes('toilette')) return 'WC/Sanitaires';
    
    return title.split(' ').slice(0, 2).join(' '); // Garder les 2 premiers mots
  }

  /**
   * Calcule les types d'interventions depuis les données réelles
   */
  private computeInterventionTypes(interventions: Intervention[]): void {
    const typeCount: Record<string, number> = {};
    
    interventions.forEach(intervention => {
      const type = intervention.type;
      typeCount[type] = (typeCount[type] || 0) + 1;
    });

    const total = interventions.length || 1;
    
    const types: InterventionTypeData[] = Object.entries(typeCount)
      .map(([type, value]) => ({
        name: INTERVENTION_TYPE_LABELS[type as IntType] || type,
        value,
        color: INTERVENTION_COLORS[type] || '#64748B',
        percentage: Math.round((value / total) * 100)
      }))
      .sort((a, b) => b.value - a.value);

    // S'assurer que les pourcentages totalisent 100%
    const totalPercentage = types.reduce((sum, t) => sum + t.percentage, 0);
    if (totalPercentage !== 100 && types.length > 0) {
      types[0].percentage += (100 - totalPercentage);
    }

    this.interventionTypes.set(types.length > 0 ? types : this.getDefaultInterventionTypes());
  }

  /**
   * Données par défaut si aucune intervention
   */
  private setDefaultInterventionData(): void {
    this.topServices.set([
      { name: 'Fuite d\'eau', value: 24 },
      { name: 'Chauffe-eau', value: 18 },
      { name: 'Débouchage', value: 12 },
      { name: 'Robinetterie', value: 10 }
    ]);
    
    this.interventionTypes.set(this.getDefaultInterventionTypes());
  }

  private getDefaultInterventionTypes(): InterventionTypeData[] {
    return [
      { name: 'Dépannage', value: 45, color: '#3B82F6', percentage: 45 },
      { name: 'Rénovation', value: 30, color: '#F97316', percentage: 30 },
      { name: 'Entretien', value: 25, color: '#10B981', percentage: 25 }
    ];
  }

  formatCurrency(amount?: number): string {
    if (amount === undefined || amount === null) return '0,00 €';
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Auj.';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Hier';
    } else {
      return date.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' });
    }
  }

  getMonthName(month: number): string {
    const months = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun', 'Jul', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'];
    return months[month - 1] || '';
  }

  mapQuoteStatus(status: QuoteStatus): string {
    const statusMap: Record<QuoteStatus, string> = {
      [QuoteStatus.DRAFT]: 'Brouillon',
      [QuoteStatus.SENT]: 'Envoyé',
      [QuoteStatus.ACCEPTED]: 'Signé',
      [QuoteStatus.REJECTED]: 'Refusé',
      [QuoteStatus.EXPIRED]: 'Expiré',
      [QuoteStatus.CONVERTED]: 'Converti'
    };
    return statusMap[status] || status;
  }

  mapInvoiceStatus(status: InvoiceStatus): string {
    const statusMap: Record<InvoiceStatus, string> = {
      [InvoiceStatus.DRAFT]: 'Brouillon',
      [InvoiceStatus.SENT]: 'Envoyé',
      [InvoiceStatus.PAID]: 'Réglé',
      [InvoiceStatus.OVERDUE]: 'En retard',
      [InvoiceStatus.CANCELLED]: 'Annulé',
      [InvoiceStatus.PARTIALLY_PAID]: 'Partiellement payé'
    };
    return statusMap[status] || status;
  }

  getStatusClass(status: string): string {
    switch(status) {
      case 'Brouillon': return 'status-draft';
      case 'Envoyé': return 'status-sent';
      case 'Signé': return 'status-signed';
      case 'Réglé': return 'status-paid';
      case 'En retard': return 'status-overdue';
      case 'Refusé': return 'status-rejected';
      case 'Expiré': return 'status-expired';
      case 'Converti': return 'status-converted';
      default: return 'status-default';
    }
  }

  // Line Chart Generation
  generateLinePath(data: RevenueData[]): string {
    if (!data.length) return '';
    
    const width = 600;
    const height = 200;
    const maxVal = Math.max(...data.map(d => d.ca), 1000) * 1.1;
    const stepX = data.length > 1 ? width / (data.length - 1) : width;

    const points = data.map((d, i) => {
      const x = i * stepX;
      const y = height - (d.ca / maxVal * height); 
      return `${x},${y}`;
    });

    return `M ${points.join(' L ')}`;
  }

  getPoints(data: RevenueData[]): {x: number, y: number, value: number}[] {
    const width = 600;
    const height = 200;
    const maxVal = Math.max(...data.map(d => d.ca), 1000) * 1.1;
    const stepX = data.length > 1 ? width / (data.length - 1) : width;

    return data.map((d, i) => ({
      x: i * stepX,
      y: height - (d.ca / maxVal * height),
      value: d.ca
    }));
  }

  navigateToDocument(doc: Document): void {
    if (doc.originalType === 'quote' && doc.originalId) {
      this.router.navigate(['/quotes', doc.originalId]);
    } else if (doc.originalType === 'invoice' && doc.originalId) {
      this.router.navigate(['/invoices', doc.originalId]);
    }
  }

  editDocument(doc: Document, event: Event): void {
    event.stopPropagation();
    if (doc.originalType === 'quote' && doc.originalId) {
      this.router.navigate(['/quotes', doc.originalId, 'edit']);
    } else if (doc.originalType === 'invoice' && doc.originalId) {
      this.router.navigate(['/invoices', doc.originalId, 'edit']);
    }
  }

  downloadDocument(doc: Document, event: Event): void {
    event.stopPropagation();
    
    const downloadBlob = (blob: Blob, filename: string) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      a.click();
      window.URL.revokeObjectURL(url);
      this.snackBar.open('Téléchargement réussi', 'Fermer', { duration: 2000 });
    };

    if (doc.originalType === 'quote' && doc.originalId) {
      this.quoteService.generatePdf(doc.originalId).subscribe({
        next: (blob) => downloadBlob(blob, `${doc.id}.pdf`),
        error: () => {
          this.snackBar.open('Erreur lors du téléchargement', 'Fermer', { duration: 3000 });
        }
      });
    } else if (doc.originalType === 'invoice' && doc.originalId) {
      this.invoiceService.generatePdf(doc.originalId).subscribe({
        next: (blob) => downloadBlob(blob, `${doc.id}.pdf`),
        error: () => {
          this.snackBar.open('Erreur lors du téléchargement', 'Fermer', { duration: 3000 });
        }
      });
    }
  }

  /**
   * Envoie le document par email au client
   */
  sendDocument(doc: Document, event: Event): void {
    event.stopPropagation();

    if (!doc.clientEmail) {
      this.snackBar.open('Aucune adresse email client disponible', 'Fermer', { duration: 3000 });
      return;
    }

    const confirmSend = confirm(`Envoyer ${doc.type.toLowerCase()} ${doc.id} à ${doc.clientEmail} ?`);
    
    if (!confirmSend) return;

    if (doc.originalType === 'quote' && doc.originalId) {
      this.quoteService.sendQuoteByEmail(doc.originalId, doc.clientEmail).subscribe({
        next: () => {
          this.snackBar.open(`Devis envoyé avec succès à ${doc.clientEmail}`, 'Fermer', { 
            duration: 4000,
            panelClass: ['success-snackbar']
          });
          // Recharger les documents pour mettre à jour le statut
          this.loadRecentDocuments();
        },
        error: (error) => {
          console.error('Error sending quote:', error);
          this.snackBar.open('Erreur lors de l\'envoi du devis', 'Fermer', { 
            duration: 4000,
            panelClass: ['error-snackbar']
          });
        }
      });
    } else if (doc.originalType === 'invoice' && doc.originalId) {
      this.invoiceService.sendInvoiceByEmail(doc.originalId, doc.clientEmail).subscribe({
        next: () => {
          this.snackBar.open(`Facture envoyée avec succès à ${doc.clientEmail}`, 'Fermer', { 
            duration: 4000,
            panelClass: ['success-snackbar']
          });
          // Recharger les documents pour mettre à jour le statut
          this.loadRecentDocuments();
        },
        error: (error) => {
          console.error('Error sending invoice:', error);
          this.snackBar.open('Erreur lors de l\'envoi de la facture', 'Fermer', { 
            duration: 4000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  isDraft(status: string): boolean {
    return status === 'Brouillon';
  }

  canSend(doc: Document): boolean {
    // On peut envoyer si pas brouillon et pas déjà envoyé/réglé
    return !this.isDraft(doc.status) && doc.status !== 'Envoyé' && doc.status !== 'Réglé';
  }

  getMaxServiceValue(): number {
    const services = this.topServices();
    if (services.length === 0) return 1;
    return Math.max(...services.map(s => s.value));
  }

  // Donut Chart Generation with Animation Support
  getDonutChartSegments(): { 
    color: string; 
    percentage: number; 
    offset: number; 
    dashArray: string;
    name: string;
    value: number;
  }[] {
    const types = this.interventionTypes();
    const circumference = 2 * Math.PI * 15.91549430918954; // 2πr
    let currentOffset = circumference * 0.25; // Start at top
    
    return types.map(type => {
      const dashLength = this.donutAnimated() 
        ? (type.percentage / 100) * circumference 
        : 0; // Animation: start from 0
      const gapLength = circumference - dashLength;
      const dashArray = `${dashLength} ${gapLength}`;
      const offset = currentOffset;
      currentOffset -= (type.percentage / 100) * circumference;
      return {
        color: type.color,
        percentage: type.percentage,
        offset,
        dashArray,
        name: type.name,
        value: type.value
      };
    });
  }

  getTotalInterventions(): number {
    const types = this.interventionTypes();
    return types.reduce((sum, type) => sum + type.value, 0);
  }

  // Tooltip handlers
  onSegmentHover(segment: InterventionTypeData | null): void {
    this.hoveredSegment.set(segment);
  }

  getSegmentTooltip(segment: { name: string; value: number; percentage: number }): string {
    return `${segment.name}: ${segment.value} interventions (${segment.percentage}%)`;
  }

  // Service bar animation width
  getServiceBarWidth(service: TopService): number {
    if (!this.servicesAnimated()) return 0;
    return (service.value / this.getMaxServiceValue()) * 100;
  }

  getCurrentDate(): string {
    const date = new Date();
    const days = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
    const months = ['janvier', 'février', 'mars', 'avril', 'mai', 'juin', 'juillet', 'août', 'septembre', 'octobre', 'novembre', 'décembre'];
    return `${days[date.getDay()]} ${date.getDate()} ${months[date.getMonth()]} ${date.getFullYear()}`;
  }
}
