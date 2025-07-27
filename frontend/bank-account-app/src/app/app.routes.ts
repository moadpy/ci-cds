import { Routes } from '@angular/router';
import { BankAccountListComponent } from './components/bank-account-list/bank-account-list';
import { BankAccountFormComponent } from './components/bank-account-form/bank-account-form';

export const routes: Routes = [
  { path: '', redirectTo: '/accounts', pathMatch: 'full' },
  { path: 'accounts', component: BankAccountListComponent },
  { path: 'accounts/new', component: BankAccountFormComponent },
  { path: 'accounts/edit/:id', component: BankAccountFormComponent },
  { path: '**', redirectTo: '/accounts' }
];
