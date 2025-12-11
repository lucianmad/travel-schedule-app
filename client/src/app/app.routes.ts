import { Routes } from '@angular/router';
import {Register} from './components/register/register';
import {Home} from './components/home/home';
import {Login} from './components/login/login';

export const routes: Routes = [
  { path: 'register', component: Register },
  { path: 'login', component: Login },
  { path: 'home', component: Home },
];
