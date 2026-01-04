import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { MatRippleModule } from '@angular/material/core';
import { TranslateModule } from '@ngx-translate/core';
import { CapitalizePipe } from '../../pipes/capitalize.pipe';

/**
 * Interface définissant la structure d'un élément de menu
 */
export interface MenuItem {
  label: string;          // Clé de traduction pour le label
  icon: string;           // Nom de l'icône Material Icons
  route: string;          // Route de navigation
  badge?: number;         // Badge optionnel (nombre de notifications)
  ariaLabel?: string;     // Label pour l'accessibilité
}

/**
 * Interface définissant une section de menu
 */
export interface MenuSection {
  title?: string;         // Titre de la section (optionnel pour navigation principale)
  items: MenuItem[];      // Liste des items de la section
}

/**
 * Composant Sidebar
 *
 * Affiche une barre de navigation latérale moderne avec :
 * - Header avec avatar et nom d'entreprise
 * - Navigation organisée en sections
 * - Support du highlight automatique de la route active
 * - Responsive (overlay sur mobile, fixe sur desktop)
 * - Support des badges de notification
 */
@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatListModule,
    MatIconModule,
    MatBadgeModule,
    MatRippleModule,
    TranslateModule,
    CapitalizePipe
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  /**
   * Contrôle l'état d'ouverture de la sidebar (utile pour mobile)
   */
  @Input() isOpen = false;

  /**
   * Nom de l'entreprise à afficher dans le header
   */
  @Input() companyName = 'mon entreprise';

  /**
   * Initiales à afficher dans l'avatar
   */
  @Input() initials = 'ME';

  /**
   * Événement émis lors de la fermeture de la sidebar (mobile)
   */
  @Output() onClose = new EventEmitter<void>();

  /**
   * Configuration des sections de menu
   * Organisées par catégories : Navigation principale, Ventes, Achats, Comptabilité
   */
  menuSections: MenuSection[] = [
    // Navigation principale (sans titre de section)
    {
      items: [
        {
          label: 'tableau de bord',
          icon: 'assessment',
          route: '/dashboard',
          ariaLabel: 'Accéder au tableau de bord'
        },
        {
          label: 'chantiers',
          icon: 'construction',
          route: '/chantiers',
          ariaLabel: 'Gérer les chantiers'
        }
      ]
    },
    // Section VENTES
    {
      title: 'ventes',
      items: [
        {
          label: 'devis',
          icon: 'description',
          route: '/quotes',
          ariaLabel: 'Gérer les devis'
        },
        {
          label: 'factures',
          icon: 'receipt',
          route: '/invoices',
          ariaLabel: 'Gérer les factures'
        },
        {
          label: 'clients',
          icon: 'people',
          route: '/clients',
          ariaLabel: 'Gérer les clients'
        },
        {
          label: 'bibliotheque',
          icon: 'folder',
          route: '/bibliotheque',
          ariaLabel: 'Accéder à la bibliothèque'
        }
      ]
    },
    // Section ACHATS
    {
      title: 'achats',
      items: [
        {
          label: 'factures d achats',
          icon: 'shopping_cart',
          route: '/achats/factures',
          ariaLabel: 'Gérer les factures d\'achats'
        }
      ]
    },
    // Section COMPTABILITÉ
    {
      title: 'comptabilite',
      items: [
        {
          label: 'transactions',
          icon: 'account_balance',
          route: '/comptabilite/transactions',
          ariaLabel: 'Voir les transactions'
        },
        {
          label: 'livre des recettes',
          icon: 'menu_book',
          route: '/comptabilite/livre-recettes',
          ariaLabel: 'Accéder au livre des recettes'
        },
        {
          label: 'reglages',
          icon: 'settings',
          route: '/reglages',
          ariaLabel: 'Modifier les réglages'
        },
        {
          label: 'parrainage',
          icon: 'card_giftcard',
          route: '/parrainage',
          ariaLabel: 'Programme de parrainage'
        },
        {
          label: 'besoin d aide',
          icon: 'help',
          route: '/aide',
          ariaLabel: 'Obtenir de l\'aide'
        }
      ]
    }
  ];

  /**
   * Ferme la sidebar (utilisé sur mobile)
   */
  closeSidebar(): void {
    this.onClose.emit();
  }

  /**
   * Gère le clic sur un item de menu
   * Ferme la sidebar sur mobile après navigation
   */
  onItemClick(): void {
    // Sur mobile, ferme la sidebar après avoir cliqué sur un item
    if (window.innerWidth < 768) {
      this.closeSidebar();
    }
  }
}
