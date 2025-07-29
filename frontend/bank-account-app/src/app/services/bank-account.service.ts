import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { BankAccount } from '../models/bank-account.model';
//     // Navigate to the edit form with the account ID
@Injectable({
  providedIn: 'root'
})
export class BankAccountService {
  private apiUrl = 'http://localhost:8080/api/accounts';

  // 1. Create a BehaviorSubject to hold the list of accounts.
  // This will be our single source of truth.
  private accountsSubject = new BehaviorSubject<BankAccount[]>([]);

  // 2. Expose the list of accounts as an observable.
  // Components will subscribe to this to get the data.
  public accounts$ = this.accountsSubject.asObservable();

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  // 3. Load all accounts from the API and update the BehaviorSubject.
  // This method will be called once to populate our central store.
  public loadAllAccounts(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(this.apiUrl).pipe(
      tap(accounts => this.accountsSubject.next(accounts))
    );
  }

  getAccountById(id: number): Observable<BankAccount> {
    return this.http.get<BankAccount>(`${this.apiUrl}/${id}`);
  }

  // 4. When an account is created, add it to the central store.
  createAccount(account: BankAccount): Observable<BankAccount> {
    return this.http.post<BankAccount>(this.apiUrl, account, this.httpOptions).pipe(
      tap(newAccount => {
        const currentAccounts = this.accountsSubject.getValue();
        this.accountsSubject.next([...currentAccounts, newAccount]);
      })
    );
  }

  updateAccount(id: number, account: BankAccount): Observable<BankAccount> {
    return this.http.put<BankAccount>(`${this.apiUrl}/${id}`, account, this.httpOptions).pipe(
      tap(updatedAccount => {
        const currentAccounts = this.accountsSubject.getValue();
        const index = currentAccounts.findIndex(a => a.id === id);
        if (index !== -1) {
          currentAccounts[index] = updatedAccount;
          this.accountsSubject.next([...currentAccounts]);
        }
      })
    );
  }

  // 5. When an account is deleted, remove it from the central store.
  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        const currentAccounts = this.accountsSubject.getValue();
        const updatedAccounts = currentAccounts.filter(a => a.id !== id);
        this.accountsSubject.next(updatedAccounts);
      })
    );
  }
}
