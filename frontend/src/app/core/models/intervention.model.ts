/**
 * Statuts d'intervention simplifiés pour plombiers
 */
export enum InterventionStatus {
  URGENT = 'URGENT',
  PLANNED = 'PLANNED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  TO_INVOICE = 'TO_INVOICE'
}

/**
 * Labels français des statuts
 */
export const INTERVENTION_STATUS_LABELS: Record<InterventionStatus, string> = {
  [InterventionStatus.URGENT]: 'Urgent',
  [InterventionStatus.PLANNED]: 'Planifiée',
  [InterventionStatus.IN_PROGRESS]: 'En cours',
  [InterventionStatus.COMPLETED]: 'Terminée',
  [InterventionStatus.TO_INVOICE]: 'À facturer'
};

/**
 * Couleurs des statuts (Material Design)
 */
export const INTERVENTION_STATUS_COLORS: Record<InterventionStatus, { bg: string; text: string; icon: string }> = {
  [InterventionStatus.URGENT]: { bg: '#ffebee', text: '#c62828', icon: 'warning' },
  [InterventionStatus.PLANNED]: { bg: '#e3f2fd', text: '#1565c0', icon: 'event' },
  [InterventionStatus.IN_PROGRESS]: { bg: '#fff3e0', text: '#e65100', icon: 'engineering' },
  [InterventionStatus.COMPLETED]: { bg: '#e8f5e9', text: '#2e7d32', icon: 'check_circle' },
  [InterventionStatus.TO_INVOICE]: { bg: '#f3e5f5', text: '#7b1fa2', icon: 'receipt' }
};

/**
 * Types d'intervention plomberie
 */
export enum InterventionType {
  DEPANNAGE = 'DEPANNAGE',
  RENOVATION = 'RENOVATION',
  ENTRETIEN = 'ENTRETIEN',
  INSTALLATION = 'INSTALLATION',
  DIAGNOSTIC = 'DIAGNOSTIC'
}

/**
 * Labels des types d'intervention
 */
export const INTERVENTION_TYPE_LABELS: Record<InterventionType, string> = {
  [InterventionType.DEPANNAGE]: 'Dépannage',
  [InterventionType.RENOVATION]: 'Rénovation',
  [InterventionType.ENTRETIEN]: 'Entretien',
  [InterventionType.INSTALLATION]: 'Installation',
  [InterventionType.DIAGNOSTIC]: 'Diagnostic'
};

/**
 * Icônes des types d'intervention
 */
export const INTERVENTION_TYPE_ICONS: Record<InterventionType, string> = {
  [InterventionType.DEPANNAGE]: 'build',
  [InterventionType.RENOVATION]: 'construction',
  [InterventionType.ENTRETIEN]: 'handyman',
  [InterventionType.INSTALLATION]: 'plumbing',
  [InterventionType.DIAGNOSTIC]: 'search'
};

/**
 * Modèle d'intervention
 */
export interface Intervention {
  id: number;
  reference: string;
  clientId: number;
  clientName: string;
  clientPhone?: string;
  clientAddress?: string;
  type: InterventionType;
  status: InterventionStatus;
  title: string;
  description?: string;
  scheduledDate?: string;
  scheduledTime?: string;
  completedDate?: string;
  estimatedDuration?: number; // en minutes
  quoteId?: number;
  quoteNumber?: string;
  quoteAmount?: number;
  invoiceId?: number;
  invoiceNumber?: string;
  invoiceAmount?: number;
  notes?: string;
  createdAt: string;
  updatedAt?: string;
}

/**
 * Les données fictives sont maintenant centralisées dans :
 * @see src/app/core/mocks/mock-data.ts
 */

