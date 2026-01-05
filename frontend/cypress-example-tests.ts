/**
 * 🧪 EXEMPLES DE TESTS CYPRESS - ERP-LITE
 * 
 * Ces tests sont des exemples prêts à l'emploi pour Cypress.
 * Pour les utiliser :
 * 
 * 1. Installer Cypress :
 *    npm install --save-dev cypress @cypress/schematic
 *    ng add @cypress/schematic
 * 
 * 2. Créer les fichiers dans cypress/e2e/ :
 *    - auth.cy.ts
 *    - dashboard.cy.ts
 *    - clients.cy.ts
 *    - quotes.cy.ts
 *    - invoices.cy.ts
 * 
 * 3. Copier le contenu correspondant ci-dessous
 * 
 * 4. Lancer les tests :
 *    npx cypress open    (interface visuelle)
 *    npx cypress run     (headless)
 */

// ============================================================
// FICHIER 1 : cypress/e2e/auth.cy.ts
// ============================================================

describe('Authentication Tests', () => {
  const TEST_USER = {
    email: 'test@erp-lite.com',
    password: 'Test123!'
  };

  beforeEach(() => {
    // Clear localStorage et cookies avant chaque test
    cy.clearLocalStorage();
    cy.clearCookies();
    cy.visit('/auth/login');
  });

  describe('Login', () => {
    it('should display login form', () => {
      cy.get('app-login').should('exist');
      cy.contains('Connexion').should('be.visible');
      cy.get('input[formControlName="email"]').should('be.visible');
      cy.get('input[formControlName="password"]').should('be.visible');
      cy.get('button[type="submit"]').should('be.visible');
    });

    it('should show validation errors for empty fields', () => {
      cy.get('button[type="submit"]').click();
      cy.contains('L\'email est requis').should('be.visible');
      cy.contains('Le mot de passe est requis').should('be.visible');
    });

    it('should show error for invalid email format', () => {
      cy.get('input[formControlName="email"]').type('invalid-email');
      cy.get('input[formControlName="email"]').blur();
      cy.contains('Format d\'email invalide').should('be.visible');
    });

    it('should login successfully with valid credentials', () => {
      cy.intercept('POST', '**/api/v1/auth/login').as('loginRequest');

      cy.get('input[formControlName="email"]').type(TEST_USER.email);
      cy.get('input[formControlName="password"]').type(TEST_USER.password);
      cy.get('button[type="submit"]').click();

      // Wait for API call
      cy.wait('@loginRequest').its('response.statusCode').should('eq', 200);

      // Should redirect to dashboard
      cy.url().should('include', '/dashboard');

      // Token should be stored
      cy.window().then((win) => {
        const token = win.localStorage.getItem('accessToken');
        expect(token).to.exist;
        expect(token).to.be.a('string');
        expect(token).to.have.length.greaterThan(0);
      });

      // User name should be displayed
      cy.contains('Bonjour').should('be.visible');
    });

    it('should show error for invalid credentials', () => {
      cy.intercept('POST', '**/api/v1/auth/login', {
        statusCode: 401,
        body: { message: 'Identifiants invalides' }
      }).as('loginRequest');

      cy.get('input[formControlName="email"]').type('wrong@email.com');
      cy.get('input[formControlName="password"]').type('WrongPassword');
      cy.get('button[type="submit"]').click();

      cy.wait('@loginRequest');

      // Should stay on login page
      cy.url().should('include', '/auth/login');

      // Error message should be visible
      cy.contains('Identifiants invalides').should('be.visible');
    });

    it('should toggle password visibility', () => {
      cy.get('input[formControlName="password"]').should('have.attr', 'type', 'password');
      
      cy.get('button[aria-label*="password"]').click();
      cy.get('input[formControlName="password"]').should('have.attr', 'type', 'text');
      
      cy.get('button[aria-label*="password"]').click();
      cy.get('input[formControlName="password"]').should('have.attr', 'type', 'password');
    });
  });

  describe('Logout', () => {
    beforeEach(() => {
      // Login first
      cy.get('input[formControlName="email"]').type(TEST_USER.email);
      cy.get('input[formControlName="password"]').type(TEST_USER.password);
      cy.get('button[type="submit"]').click();
      cy.url().should('include', '/dashboard');
    });

    it('should logout successfully', () => {
      cy.intercept('POST', '**/api/v1/auth/logout').as('logoutRequest');

      // Find and click logout button
      cy.get('[data-testid="logout-button"]').click();

      cy.wait('@logoutRequest');

      // Should redirect to login
      cy.url().should('include', '/auth/login');

      // Token should be removed
      cy.window().then((win) => {
        const token = win.localStorage.getItem('accessToken');
        expect(token).to.not.exist;
      });
    });
  });

  describe('Auth Guard', () => {
    it('should redirect to login when accessing protected route without auth', () => {
      cy.visit('/dashboard');
      cy.url().should('include', '/auth/login');
    });

    it('should allow access to protected routes when authenticated', () => {
      // Login
      cy.get('input[formControlName="email"]').type(TEST_USER.email);
      cy.get('input[formControlName="password"]').type(TEST_USER.password);
      cy.get('button[type="submit"]').click();
      cy.url().should('include', '/dashboard');

      // Try to access other protected routes
      cy.visit('/clients');
      cy.url().should('include', '/clients');

      cy.visit('/quotes');
      cy.url().should('include', '/quotes');

      cy.visit('/invoices');
      cy.url().should('include', '/invoices');
    });
  });
});

// ============================================================
// FICHIER 2 : cypress/e2e/dashboard.cy.ts
// ============================================================

describe('Dashboard Tests', () => {
  beforeEach(() => {
    // Login helper
    cy.loginAs('test@erp-lite.com', 'Test123!');
    cy.visit('/dashboard');
  });

  describe('KPI Cards', () => {
    it('should display all 4 KPI cards', () => {
      cy.get('.kpi-card').should('have.length', 4);
    });

    it('should display revenue KPI with correct data', () => {
      cy.intercept('GET', '**/api/v1/dashboard/stats', {
        statusCode: 200,
        body: {
          totalRevenue: 125340.50,
          unpaidInvoices: 15,
          pendingQuotes: 8,
          upcomingInterventions: 12
        }
      }).as('getStats');

      cy.wait('@getStats');

      cy.contains('Chiffre d\'affaires').should('be.visible');
      cy.contains('125 340,50 €').should('be.visible');
    });

    it('should animate KPI cards on load', () => {
      cy.get('.kpi-card').first().should('have.class', 'animate-fade-in');
    });
  });

  describe('Monthly Revenue Chart', () => {
    it('should display line chart with 6 months', () => {
      cy.intercept('GET', '**/api/v1/dashboard/monthly-revenue', {
        statusCode: 200,
        body: [
          { month: 'Janvier', revenue: 12500 },
          { month: 'Février', revenue: 15000 },
          { month: 'Mars', revenue: 18000 },
          { month: 'Avril', revenue: 14000 },
          { month: 'Mai', revenue: 20000 },
          { month: 'Juin', revenue: 22000 }
        ]
      }).as('getRevenue');

      cy.wait('@getRevenue');

      cy.get('.line-chart').should('be.visible');
      cy.get('.line-chart path').should('have.attr', 'stroke', '#2563EB');
    });

    it('should show tooltip on hover', () => {
      cy.get('.line-chart circle').first().trigger('mouseover');
      cy.get('.chart-tooltip').should('be.visible');
      cy.get('.chart-tooltip').should('contain', '€');
    });
  });

  describe('Donut Chart', () => {
    it('should display intervention types donut chart', () => {
      cy.get('.donut-chart').should('be.visible');
      cy.get('.donut-chart path').should('have.length.at.least', 3);
    });

    it('should show tooltip on segment hover', () => {
      cy.get('.donut-chart path').first().trigger('mouseover');
      cy.get('.donut-tooltip').should('be.visible');
    });

    it('should display legend with percentages', () => {
      cy.get('.donut-legend').should('be.visible');
      cy.get('.donut-legend-item').should('have.length.at.least', 3);
      cy.get('.donut-legend-item').first().should('contain', '%');
    });
  });

  describe('Recent Documents Table', () => {
    it('should display recent documents', () => {
      cy.intercept('GET', '**/api/v1/quotes*', {
        statusCode: 200,
        body: {
          content: [
            {
              id: 1,
              number: 'DEV-2026-001',
              client: { contactFirstName: 'Jean', contactLastName: 'Dupont' },
              date: '2026-01-01',
              totalAmount: 1500.00,
              status: 'SENT'
            }
          ]
        }
      }).as('getQuotes');

      cy.intercept('GET', '**/api/v1/invoices*', {
        statusCode: 200,
        body: {
          content: [
            {
              id: 1,
              number: 'FAC-2026-001',
              client: { contactFirstName: 'Marie', contactLastName: 'Martin' },
              date: '2026-01-02',
              totalAmount: 2500.00,
              status: 'PAID'
            }
          ]
        }
      }).as('getInvoices');

      cy.wait(['@getQuotes', '@getInvoices']);

      cy.get('.documents-table').should('be.visible');
      cy.get('.documents-table tbody tr').should('have.length.at.least', 1);
    });

    it('should send document by email', () => {
      cy.intercept('POST', '**/api/v1/quotes/*/send-email').as('sendEmail');

      cy.get('.documents-table tbody tr').first().find('[aria-label="Envoyer"]').click();
      cy.get('mat-dialog-container').should('be.visible');
      cy.contains('Confirmer').click();

      cy.wait('@sendEmail').its('response.statusCode').should('eq', 200);
      cy.contains('Document envoyé avec succès').should('be.visible');
    });
  });

  describe('Top Services Bar Chart', () => {
    it('should display top services', () => {
      cy.get('.top-services').should('be.visible');
      cy.get('.service-bar').should('have.length.at.least', 1);
    });

    it('should animate bars on load', () => {
      cy.get('.service-bar').first().should('have.class', 'animate-grow');
    });
  });
});

// ============================================================
// FICHIER 3 : cypress/e2e/clients.cy.ts
// ============================================================

describe('Clients Tests', () => {
  beforeEach(() => {
    cy.loginAs('test@erp-lite.com', 'Test123!');
    cy.visit('/clients');
  });

  describe('Clients List', () => {
    it('should display clients list', () => {
      cy.intercept('GET', '**/api/v1/clients*').as('getClients');
      cy.wait('@getClients');

      cy.get('mat-table').should('be.visible');
      cy.get('mat-header-row').should('be.visible');
    });

    it('should display correct columns', () => {
      const columns = ['Nom', 'Email', 'Téléphone', 'Ville', 'Actions'];
      columns.forEach(column => {
        cy.contains('mat-header-cell', column).should('be.visible');
      });
    });

    it('should paginate results', () => {
      cy.intercept('GET', '**/api/v1/clients?page=1*').as('getPage2');
      
      cy.get('mat-paginator').should('be.visible');
      cy.get('[aria-label="Next page"]').click();
      
      cy.wait('@getPage2');
    });
  });

  describe('Search', () => {
    it('should search clients by name', () => {
      cy.intercept('GET', '**/api/v1/clients?*search=dupont*').as('searchClients');

      cy.get('[placeholder="Rechercher"]').type('dupont');
      cy.wait('@searchClients');

      cy.get('mat-table mat-row').should('have.length.at.least', 1);
    });

    it('should debounce search input', () => {
      cy.intercept('GET', '**/api/v1/clients?*search=*').as('search');

      cy.get('[placeholder="Rechercher"]').type('test');
      cy.get('@search.all').should('have.length', 0);
      
      cy.wait(400); // Wait for debounce
      cy.get('@search.all').should('have.length', 1);
    });
  });

  describe('Create Client', () => {
    it('should open create client form', () => {
      cy.contains('button', 'Nouveau client').click();
      cy.url().should('include', '/clients/new');
      cy.get('form').should('be.visible');
    });

    it('should create a new client', () => {
      cy.intercept('POST', '**/api/v1/clients', {
        statusCode: 201,
        body: { id: 999, contactFirstName: 'Test', contactLastName: 'E2E' }
      }).as('createClient');

      cy.contains('button', 'Nouveau client').click();

      cy.get('[formControlName="contactFirstName"]').type('Test');
      cy.get('[formControlName="contactLastName"]').type('E2E');
      cy.get('[formControlName="email"]').type('test-e2e@example.com');
      cy.get('[formControlName="phone"]').type('0612345678');
      cy.get('[formControlName="city"]').type('Paris');
      cy.get('[formControlName="postalCode"]').type('75001');

      cy.get('button[type="submit"]').click();

      cy.wait('@createClient');
      cy.contains('Client créé avec succès').should('be.visible');
    });

    it('should show validation errors', () => {
      cy.contains('button', 'Nouveau client').click();
      cy.get('button[type="submit"]').click();

      cy.contains('Le nom est requis').should('be.visible');
      cy.contains('L\'email est requis').should('be.visible');
    });
  });

  describe('Edit Client', () => {
    it('should edit client', () => {
      cy.intercept('GET', '**/api/v1/clients/1').as('getClient');
      cy.intercept('PUT', '**/api/v1/clients/1').as('updateClient');

      cy.get('mat-table mat-row').first().find('[aria-label="Modifier"]').click();
      cy.wait('@getClient');

      cy.get('[formControlName="email"]').clear().type('updated@example.com');
      cy.get('button[type="submit"]').click();

      cy.wait('@updateClient');
      cy.contains('Client modifié avec succès').should('be.visible');
    });
  });

  describe('Delete Client', () => {
    it('should delete client with confirmation', () => {
      cy.intercept('DELETE', '**/api/v1/clients/1').as('deleteClient');

      cy.get('mat-table mat-row').first().find('[aria-label="Supprimer"]').click();
      
      cy.get('mat-dialog-container').should('be.visible');
      cy.contains('Êtes-vous sûr').should('be.visible');
      cy.get('mat-dialog-container').contains('button', 'Supprimer').click();

      cy.wait('@deleteClient');
      cy.contains('Client supprimé avec succès').should('be.visible');
    });
  });
});

// ============================================================
// FICHIER 4 : cypress/support/commands.ts
// ============================================================

/**
 * Custom Cypress Commands
 * Ajouter ces commandes dans cypress/support/commands.ts
 */

declare namespace Cypress {
  interface Chainable {
    /**
     * Login command
     * @example cy.loginAs('user@example.com', 'password')
     */
    loginAs(email: string, password: string): Chainable<void>;
    
    /**
     * Get by data-testid
     * @example cy.getByTestId('submit-button')
     */
    getByTestId(testId: string): Chainable<JQuery<HTMLElement>>;
  }
}

Cypress.Commands.add('loginAs', (email: string, password: string) => {
  cy.intercept('POST', '**/api/v1/auth/login').as('loginRequest');
  
  cy.visit('/auth/login');
  cy.get('input[formControlName="email"]').type(email);
  cy.get('input[formControlName="password"]').type(password);
  cy.get('button[type="submit"]').click();
  
  cy.wait('@loginRequest');
  cy.url().should('include', '/dashboard');
});

Cypress.Commands.add('getByTestId', (testId: string) => {
  return cy.get(`[data-testid="${testId}"]`);
});

// ============================================================
// FICHIER 5 : cypress.config.ts
// ============================================================

/**
 * Configuration Cypress
 * Créer ou remplacer le fichier cypress.config.ts
 */

import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:4200',
    viewportWidth: 1280,
    viewportHeight: 720,
    video: false,
    screenshotOnRunFailure: true,
    
    // Retry failed tests
    retries: {
      runMode: 2,
      openMode: 0
    },
    
    // Timeouts
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 10000,
    
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
  
  env: {
    apiUrl: 'http://localhost:8080/api/v1',
    testUser: {
      email: 'test@erp-lite.com',
      password: 'Test123!'
    }
  },
  
  // Chrome flags for better performance
  chromeWebSecurity: false,
});

// ============================================================
// INSTRUCTIONS D'INSTALLATION
// ============================================================

/**
 * ÉTAPES D'INSTALLATION COMPLÈTE
 * 
 * 1. Installer Cypress
 *    npm install --save-dev cypress @cypress/schematic
 *    ng add @cypress/schematic
 * 
 * 2. Créer la structure de dossiers
 *    mkdir -p cypress/e2e
 *    mkdir -p cypress/support
 *    mkdir -p cypress/fixtures
 * 
 * 3. Créer les fichiers de test
 *    touch cypress/e2e/auth.cy.ts
 *    touch cypress/e2e/dashboard.cy.ts
 *    touch cypress/e2e/clients.cy.ts
 *    touch cypress/e2e/quotes.cy.ts
 *    touch cypress/e2e/invoices.cy.ts
 * 
 * 4. Copier le contenu des tests ci-dessus dans les fichiers correspondants
 * 
 * 5. Copier les commandes custom dans cypress/support/commands.ts
 * 
 * 6. Copier la config dans cypress.config.ts
 * 
 * 7. Lancer les tests
 *    npx cypress open    # Interface graphique
 *    npx cypress run     # Mode headless (CI/CD)
 * 
 * 8. Résultats attendus
 *    - Tous les tests passent au vert ✅
 *    - Screenshots générés en cas d'échec
 *    - Vidéos disponibles (si activé)
 */

