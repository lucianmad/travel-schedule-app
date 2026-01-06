import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user';
import { TripComponent } from '../trip/trip';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, TripComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  user: User | null;

  constructor(private authService: AuthService) {
    this.user = this.authService.getCurrentUser();
  }

  onLogout(): void {
    this.authService.logout();
  }
}
