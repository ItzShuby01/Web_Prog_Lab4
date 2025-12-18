import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { MainAppComponent } from './components/main-app/main-app.component';
import { AuthGuard } from './guards/auth.guard';

// Add EXPORT here so main.ts can see it
export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: 'main',
    component: MainAppComponent,
    canActivate: [AuthGuard]
  },
  { path: '**', redirectTo: 'login' }
];
