import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user';
import { TripComponent } from '../trip/trip';

@Component({
  selector: 'app-home',
  standalone: true, // 2. Make sure Home is standalone
  imports: [TripComponent], // 3. Add TripComponent to imports
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  user: User;

  constructor(
    private authService: AuthService,
  ) {
    this.user = this.authService.getCurrentUser() as User;
  }

  onLogout(): void {
    this.authService.logout();
  }
}