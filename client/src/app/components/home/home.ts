import { Component } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {User} from '../../models/user';
import {map, Observable} from 'rxjs';

@Component({
  selector: 'app-home',
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
