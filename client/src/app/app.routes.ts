import { Routes } from '@angular/router';
import {Register} from './components/register/register';
import {Home} from './components/home/home';
import {Login} from './components/login/login';
import { RootRedirectGuard } from './guards/root-redirect.guard';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [RootRedirectGuard],
    component: Login
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: 'register',
    component: Register
  },
  {
    path: 'home',
    component: Home,
    canActivate: [AuthGuard]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
