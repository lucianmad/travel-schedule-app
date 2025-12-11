import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, tap } from "rxjs";
import { User } from "../models/user";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { RegisterRequest } from "../models/register-request";
import { AuthResponse } from "../models/auth-response";
import { LoginRequest } from "../models/login-request";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(response => this.handleAuthSuccess(response))
    );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => this.handleAuthSuccess(response))
    );
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  private handleAuthSuccess(response: AuthResponse): void {
    const user: User = {
      id: response.id,
      email: response.email,
      username: response.username
    };
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  private getUserFromStorage(): User | null {
    const userStr = localStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
  }
}
