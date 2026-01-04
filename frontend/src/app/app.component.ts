import { Component } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map, shareReplay, filter } from 'rxjs/operators';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from './core/services/auth.service';
import { CapitalizePipe } from './shared/pipes/capitalize.pipe';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    TranslateModule,
    CapitalizePipe,
    SidebarComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'ERP-LITE';

  isHandset$: Observable<boolean>;
  showLayout = false;

  sidebarOpen = false;

  constructor(
    private breakpointObserver: BreakpointObserver,
    private authService: AuthService,
    private translate: TranslateService,
    private router: Router
  ) {
    // Configure la langue par défaut
    this.translate.setDefaultLang('fr');
    this.translate.use('fr');

    this.isHandset$ = this.breakpointObserver.observe(Breakpoints.Handset)
      .pipe(
        map(result => result.matches),
        shareReplay()
      );

    // Détecter les routes publiques pour masquer la sidebar/toolbar
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.updateLayoutVisibility();
    });

    // Vérification initiale
    this.updateLayoutVisibility();
  }

  private updateLayoutVisibility(): void {
    const currentUrl = this.router.url;
    const isPublicRoute = currentUrl.startsWith('/auth');
    const isAuthenticated = this.authService.isAuthenticated();

    // Afficher le layout (sidebar + toolbar) uniquement si :
    // - L'utilisateur est authentifié ET
    // - On n'est PAS sur une route publique
    this.showLayout = isAuthenticated && !isPublicRoute;
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  closeSidebar(): void {
    this.sidebarOpen = false;
  }

  logout(): void {
    this.authService.logout();
  }
}
