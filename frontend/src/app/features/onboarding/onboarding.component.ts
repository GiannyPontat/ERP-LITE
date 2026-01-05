import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { StorageService } from '../../core/services/storage.service';

interface OnboardingStep {
  title: string;
  subtitle: string;
  icon: string;
}

@Component({
  selector: 'app-onboarding',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatStepperModule,
    MatProgressBarModule,
    MatSnackBarModule,
    MatDividerModule,
    MatChipsModule
  ],
  templateUrl: './onboarding.component.html',
  styleUrl: './onboarding.component.scss'
})
export class OnboardingComponent implements OnInit {
  currentStep = 0;
  companyForm!: FormGroup;
  ratesForm!: FormGroup;
  
  steps: OnboardingStep[] = [
    { title: 'Bienvenue !', subtitle: 'Configurez votre espace en 2 minutes', icon: 'waving_hand' },
    { title: 'Votre entreprise', subtitle: 'Ces infos apparaîtront sur vos devis', icon: 'business' },
    { title: 'Vos tarifs de base', subtitle: 'Personnalisez vos prix habituels', icon: 'euro' },
    { title: 'Votre premier devis', subtitle: 'Créez un devis en quelques clics', icon: 'description' },
    { title: 'C\'est parti !', subtitle: 'Tout est prêt', icon: 'rocket_launch' }
  ];

  // Tarifs par défaut plomberie
  defaultRates = {
    hourlyRate: 55,
    urgencyRate: 82.50,
    travelZone1: 35,
    travelZone2: 55,
    urgencySurcharge: 50
  };

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar,
    private storage: StorageService
  ) {}

  ngOnInit(): void {
    this.initForms();
    
    // Vérifier si l'onboarding a déjà été fait
    if (this.storage.getItem('onboarding_completed') === 'true') {
      this.router.navigate(['/dashboard']);
    }
  }

  private initForms(): void {
    this.companyForm = this.fb.group({
      companyName: ['', [Validators.required, Validators.maxLength(255)]],
      siret: ['', [Validators.pattern(/^\d{14}$/)]],
      phone: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      address: [''],
      city: [''],
      postalCode: ['']
    });

    this.ratesForm = this.fb.group({
      hourlyRate: [this.defaultRates.hourlyRate, [Validators.required, Validators.min(0)]],
      urgencyRate: [this.defaultRates.urgencyRate, [Validators.required, Validators.min(0)]],
      travelZone1: [this.defaultRates.travelZone1, [Validators.required, Validators.min(0)]],
      travelZone2: [this.defaultRates.travelZone2, [Validators.required, Validators.min(0)]]
    });
  }

  get progress(): number {
    return ((this.currentStep + 1) / this.steps.length) * 100;
  }

  nextStep(): void {
    if (this.currentStep < this.steps.length - 1) {
      // Validation par étape
      if (this.currentStep === 1 && this.companyForm.invalid) {
        this.companyForm.markAllAsTouched();
        return;
      }
      if (this.currentStep === 2 && this.ratesForm.invalid) {
        this.ratesForm.markAllAsTouched();
        return;
      }
      
      this.currentStep++;
    }
  }

  prevStep(): void {
    if (this.currentStep > 0) {
      this.currentStep--;
    }
  }

  skipOnboarding(): void {
    this.completeOnboarding();
  }

  goToCreateQuote(): void {
    this.completeOnboarding();
    this.router.navigate(['/quotes/new']);
  }

  goToDashboard(): void {
    this.completeOnboarding();
    this.router.navigate(['/dashboard']);
  }

  goToCatalog(): void {
    this.completeOnboarding();
    this.router.navigate(['/catalog']);
  }

  private completeOnboarding(): void {
    // Sauvegarder les données
    if (this.companyForm.valid) {
      this.storage.setItem('company_info', JSON.stringify(this.companyForm.value));
    }
    if (this.ratesForm.valid) {
      this.storage.setItem('default_rates', JSON.stringify(this.ratesForm.value));
    }
    
    // Marquer l'onboarding comme terminé
    this.storage.setItem('onboarding_completed', 'true');
    
    this.snackBar.open('Configuration terminée !', 'OK', { duration: 3000 });
  }

  hasError(form: FormGroup, field: string, error: string): boolean {
    const control = form.get(field);
    return control ? control.hasError(error) && control.touched : false;
  }
}

