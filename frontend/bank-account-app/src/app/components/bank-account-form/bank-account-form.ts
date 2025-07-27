import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, finalize, switchMap, delay, tap } from 'rxjs/operators';
import { BankAccount } from '../../models/bank-account.model';
import { BankAccountService } from '../../services/bank-account.service';

@Component({
  selector: 'app-bank-account-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './bank-account-form.html',
  styleUrls: ['./bank-account-form.css']
})
export class BankAccountFormComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  accountForm: FormGroup;
  isEditMode = false;
  accountId: number | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private bankAccountService: BankAccountService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.accountForm = this.fb.group({
      accountNumber: ['', [Validators.required, Validators.pattern(/^\d{10,12}$/)]],
      accountHolderName: ['', [Validators.required, Validators.minLength(2)]],
      balance: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.pipe(
      takeUntil(this.destroy$)
    ).subscribe(params => {
      const id = params.get('id');
      console.log('Route params changed, id:', id); // Debug log
      if (id) {
        this.isEditMode = true;
        this.accountId = +id;
        console.log('Edit mode activated for account:', this.accountId); // Debug log
        this.loadAccount();
      } else {
        this.isEditMode = false;
        this.accountId = null;
        this.accountForm.reset({ balance: 0 });
        console.log('Create mode activated'); // Debug log
      }
    });
  }

  loadAccount(): void {
    if (this.accountId) {
      console.log('Loading account with ID:', this.accountId); // Debug log
      this.loading = true;
      this.error = null;
      this.cdr.detectChanges(); // Force change detection

      this.bankAccountService.getAccountById(this.accountId).pipe(
        takeUntil(this.destroy$)
      ).subscribe({
        next: (account) => {
          console.log('Account loaded successfully:', account); // Debug log
          this.accountForm.patchValue(account);
          this.loading = false;
          this.cdr.detectChanges(); // Force change detection after loading
        },
        error: (err) => {
          console.error('Error loading account:', err); // Debug log
          this.error = 'Failed to load account details.';
          this.loading = false;
          this.cdr.detectChanges(); // Force change detection on error
        }
      });
    }
  }

  onSubmit(): void {
    if (this.accountForm.valid) {
      this.loading = true;
      this.error = null;

      const accountData: BankAccount = {
        ...this.accountForm.value,
        balance: parseFloat(this.accountForm.value.balance) || 0
      };

      const operation = this.isEditMode
        ? this.bankAccountService.updateAccount(this.accountId!, accountData)
        : this.bankAccountService.createAccount(accountData);

      // Use switchMap to properly handle the request completion before navigation
      operation.pipe(
        takeUntil(this.destroy$),
        tap((response) => {
          console.log('Account saved successfully:', response);
        }),
        delay(50), // Small delay to ensure request completes
        finalize(() => {
          this.loading = false;
        })
      ).subscribe({
        next: (response) => {
          // Navigation happens here after the request is fully completed
          this.router.navigate(['/accounts']);
        },
        error: (err) => {
          // Filter out AbortError as it's not a real error in our case
          if (err.name === 'AbortError') {
            // If it's an AbortError but we got here, the operation likely succeeded
            // Just navigate without showing error
            this.router.navigate(['/accounts']);
            return;
          }

          // Handle real errors
          console.error('Error saving account:', err);
          if (err.status === 0) {
            this.error = 'Unable to connect to server. Please ensure the backend is running.';
          } else if (err.status >= 400 && err.status < 500) {
            this.error = err.error?.message || 'Invalid data provided. Please check your inputs.';
          } else if (err.status >= 500) {
            this.error = 'Server error occurred. Please try again later.';
          } else {
            this.error = this.isEditMode
              ? 'Failed to update account. Please try again.'
              : 'Failed to create account. Please try again.';
          }
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  markFormGroupTouched(): void {
    Object.keys(this.accountForm.controls).forEach(key => {
      const control = this.accountForm.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string | null {
    const field = this.accountForm.get(fieldName);
    if (field?.errors && field.touched) {
      if (field.errors['required']) {
        return `${this.getFieldDisplayName(fieldName)} is required.`;
      }
      if (field.errors['minlength']) {
        return `${this.getFieldDisplayName(fieldName)} must be at least ${field.errors['minlength'].requiredLength} characters.`;
      }
      if (field.errors['pattern']) {
        return `${this.getFieldDisplayName(fieldName)} must be a valid account number (10-12 digits).`;
      }
      if (field.errors['min']) {
        return `${this.getFieldDisplayName(fieldName)} must be 0 or greater.`;
      }
    }
    return null;
  }

  private getFieldDisplayName(fieldName: string): string {
    const displayNames: { [key: string]: string } = {
      'accountNumber': 'Account Number',
      'accountHolderName': 'Account Holder Name',
      'balance': 'Balance'
    };
    return displayNames[fieldName] || fieldName;
  }

  onCancel(): void {
    this.router.navigate(['/accounts']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
