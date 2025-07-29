import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';

import { BankAccountListComponent } from './bank-account-list';
import { BankAccountService } from '../../services/bank-account.service';
import { BankAccount } from '../../models/bank-account.model';

describe('BankAccountListComponent', () => {
  let component: BankAccountListComponent;
  let fixture: ComponentFixture<BankAccountListComponent>;
  let mockBankAccountService: jasmine.SpyObj<BankAccountService>;

  const mockAccounts: BankAccount[] = [
    { id: 1, accountHolderName: 'John Doe', accountNumber: '123456789', balance: 1000 },
    { id: 2, accountHolderName: 'Jane Smith', accountNumber: '987654321', balance: 2000 }
  ];

  beforeEach(async () => {
    const bankAccountServiceSpy = jasmine.createSpyObj('BankAccountService', ['getAllAccounts', 'deleteAccount']);
    bankAccountServiceSpy.getAllAccounts.and.returnValue(of(mockAccounts));

    await TestBed.configureTestingModule({
      imports: [BankAccountListComponent, RouterTestingModule],
      providers: [
        { provide: BankAccountService, useValue: bankAccountServiceSpy },
        provideRouter([]),
        provideHttpClient()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BankAccountListComponent);
    component = fixture.componentInstance;
    mockBankAccountService = TestBed.inject(BankAccountService) as jasmine.SpyObj<BankAccountService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getAllAccounts on init', () => {
    fixture.detectChanges();
    expect(mockBankAccountService.loadAllAccounts).toHaveBeenCalled();
  });

  it('should initialize with accounts from service', () => {
    fixture.detectChanges();
    component.accounts$.subscribe(accounts => {
      expect(accounts).toEqual(mockAccounts);
    });
  });
});
