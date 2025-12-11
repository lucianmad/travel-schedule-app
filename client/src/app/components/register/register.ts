import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  registerForm: FormGroup;
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        this.router.navigate(['/home']);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
      }
    });
  }

  get email() {
    return this.registerForm.get('email');
  }
  get username() {
    return this.registerForm.get('username');
  }
  get password() {
    return this.registerForm.get('password');
  }
  get confirmPassword() {
    return this.registerForm.get('confirmPassword');
  }
}
