 import { CommonModule } from '@angular/common';
 import { FormsModule } from '@angular/forms';
 import { Router } from '@angular/router';
 import { AuthService } from '../../services/auth.service';
 import { AuthRequest } from '../../models/IAuth';
 import { Component, ChangeDetectorRef } from '@angular/core';

 @Component({
   selector: 'app-login',
   standalone: true,
   imports: [CommonModule, FormsModule],
   templateUrl: './login.component.html',
   styleUrls: ['./login.component.css']
 })
 export class LoginComponent {
   authRequest: AuthRequest = { username: '', password: '' };
   message: string = '';

   //  Inject it in the constructor
   constructor(
     private authService: AuthService,
     private router: Router,
     private cdr: ChangeDetectorRef
   ) { }

   login(): void {
     this.message = 'Attempting login...';
     this.cdr.detectChanges(); // Force update

     this.authService.login(this.authRequest).subscribe({
       next: (res) => {
         this.message = 'Login successful!';
         this.cdr.detectChanges(); // Force update
         this.router.navigate(['/main']);
       },
       error: (err) => {
         this.message = 'Login failed. Invalid credentials.';
         this.cdr.detectChanges(); // Force update
       }
     });
   }

   register(): void {
     this.message = 'Attempting registration...';
     this.cdr.detectChanges(); // Force update

     this.authService.register(this.authRequest).subscribe({
       next: (res) => {
         this.message = res; // Shows "User registered successfully"
         this.cdr.detectChanges(); // Force update
       },
       error: (err) => {
         // Handle both text and JSON error messages from backend
         const errorMsg = err.error?.message || err.error || 'Registration failed.';

         if (err.status === 409) {
           this.message = 'Registration failed: Username already exists.';
         } else {
           this.message = errorMsg;
         }
         this.cdr.detectChanges(); // Force update
       }
     });
   }
 }
