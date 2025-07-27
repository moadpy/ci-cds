import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { BankAccount } from '../../models/bank-account.model';
import { BankAccountService } from '../../services/bank-account.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-bank-account-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './bank-account-list.html',
  styleUrls: ['./bank-account-list.css']
})
export class BankAccountListComponent implements OnInit {
  // 1. The accounts$ observable will now be the direct source for our template.
  accounts$: Observable<BankAccount[]>;
  loading = false;
  error: string | null = null;

  constructor(private bankAccountService: BankAccountService) {
    // 2. Assign the observable from the service directly.
    this.accounts$ = this.bankAccountService.accounts$;
  }

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading = true;
    this.bankAccountService.loadAllAccounts().subscribe({
      next: () => this.loading = false,
      error: (err) => {
        this.error = 'Failed to load accounts.';
        this.loading = false;
        console.error('Error loading accounts:', err);
      }
    });
  }

  deleteAccount(id: number): void {
    if (confirm('Are you sure you want to delete this account?')) {
      this.bankAccountService.deleteAccount(id).subscribe({
        next: () => {
          console.log('Account deleted successfully');
        },
        error: (err) => {
          // Only log actual errors, not AbortErrors
          if (err.name !== 'AbortError') {
            console.error('Error deleting account:', err);
            this.error = 'Failed to delete account. Please try again.';
          }
        }
      });
    }
  }

  onEditClick(accountId: number): void {
    console.log('Edit button clicked for account ID:', accountId);
    console.log('Navigating to:', `/accounts/edit/${accountId}`);
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }
}
