import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatRippleModule } from '@angular/material/core';
import { QUOTE_TEMPLATES, QuoteTemplate } from '../../../core/models/quote-template.model';

@Component({
  selector: 'app-quote-template-selector',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatRippleModule
  ],
  template: `
    <div class="template-selector">
      <h2 mat-dialog-title>
        <mat-icon>description</mat-icon>
        Créer un devis
      </h2>
      
      <mat-dialog-content>
        <p class="subtitle">Choisissez un modèle ou partez de zéro</p>
        
        <div class="templates-grid">
          <!-- Option: Devis vierge -->
          <div class="template-card blank" matRipple (click)="selectTemplate(null)">
            <div class="template-icon">
              <mat-icon>add</mat-icon>
            </div>
            <div class="template-info">
              <h3>Devis vierge</h3>
              <p>Partir de zéro</p>
            </div>
          </div>
          
          <!-- Templates prédéfinis -->
          @for (template of templates; track template.id) {
            <div class="template-card" matRipple (click)="selectTemplate(template)">
              <div class="template-icon" [attr.data-category]="template.category">
                <mat-icon>{{ template.icon }}</mat-icon>
              </div>
              <div class="template-info">
                <h3>{{ template.name }}</h3>
                <p>{{ template.description }}</p>
                <span class="item-count">{{ template.items.length }} lignes</span>
              </div>
            </div>
          }
        </div>
      </mat-dialog-content>
      
      <mat-dialog-actions align="end">
        <button mat-button mat-dialog-close>Annuler</button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .template-selector {
      min-width: 500px;
      max-width: 700px;
    }
    
    h2 {
      display: flex;
      align-items: center;
      gap: 12px;
      margin: 0;
      
      mat-icon {
        color: #1565c0;
      }
    }
    
    .subtitle {
      color: #666;
      margin: 0 0 20px;
    }
    
    .templates-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
    }
    
    .template-card {
      display: flex;
      align-items: flex-start;
      gap: 16px;
      padding: 20px;
      border: 2px solid #e0e0e0;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.2s ease;
      
      &:hover {
        border-color: #1565c0;
        background-color: #f5f9ff;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(21, 101, 192, 0.15);
      }
      
      &.blank {
        border-style: dashed;
        
        .template-icon {
          background-color: #f5f5f5;
          color: #757575;
        }
        
        &:hover .template-icon {
          background-color: #e3f2fd;
          color: #1565c0;
        }
      }
    }
    
    .template-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      
      mat-icon {
        font-size: 24px;
        width: 24px;
        height: 24px;
      }
      
      &[data-category="Dépannage"] {
        background-color: #fff3e0;
        color: #e65100;
      }
      
      &[data-category="Rénovation"] {
        background-color: #e3f2fd;
        color: #1565c0;
      }
      
      &[data-category="Entretien"] {
        background-color: #e8f5e9;
        color: #2e7d32;
      }
    }
    
    .template-info {
      flex: 1;
      
      h3 {
        margin: 0 0 4px;
        font-size: 1rem;
        font-weight: 600;
        color: #212121;
      }
      
      p {
        margin: 0;
        font-size: 0.85rem;
        color: #666;
        line-height: 1.4;
      }
      
      .item-count {
        display: inline-block;
        margin-top: 8px;
        font-size: 0.75rem;
        color: #1565c0;
        background-color: #e3f2fd;
        padding: 2px 8px;
        border-radius: 10px;
      }
    }
    
    mat-dialog-content {
      max-height: 60vh;
    }
    
    @media (max-width: 600px) {
      .template-selector {
        min-width: unset;
      }
      
      .templates-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class QuoteTemplateSelectorComponent {
  templates = QUOTE_TEMPLATES;
  
  constructor(private dialogRef: MatDialogRef<QuoteTemplateSelectorComponent>) {}
  
  selectTemplate(template: QuoteTemplate | null): void {
    this.dialogRef.close(template);
  }
}

