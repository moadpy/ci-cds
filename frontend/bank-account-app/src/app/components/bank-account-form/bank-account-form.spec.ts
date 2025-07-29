import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { BankAccountFormComponent } from './bank-account-form';
import { BankAccountService } from '../../services/bank-account.service';

describe('BankAccountFormComponent', () => {
  let component: BankAccountFormComponent;
  let fixture: ComponentFixture<BankAccountFormComponent>;
  let mockBankAccountService: jasmine.SpyObj<BankAccountService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const bankAccountServiceSpy = jasmine.createSpyObj('BankAccountService', ['createAccount', 'updateAccount', 'getAccountById']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [BankAccountFormComponent, ReactiveFormsModule],
      providers: [
        { provide: BankAccountService, useValue: bankAccountServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: { paramMap: of(new Map()) } },
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BankAccountFormComponent);
    component = fixture.componentInstance;
    mockBankAccountService = TestBed.inject(BankAccountService) as jasmine.SpyObj<BankAccountService>;
    mockRouter = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {
    fixture.detectChanges();
    expect(component.accountForm).toBeDefined();
    expect(component.accountForm.get('accountHolderName')?.value).toBe('');
    expect(component.accountForm.get('accountNumber')?.value).toBe('');
    expect(component.accountForm.get('balance')?.value).toBe('');
  });

  it('should validate required fields', () => {
    fixture.detectChanges();
    component.accountForm.patchValue({
      accountHolderName: '',
      accountNumber: '',
      balance: ''
    });

    expect(component.accountForm.valid).toBeFalsy();
    expect(component.accountForm.get('accountHolderName')?.hasError('required')).toBeTruthy();
    expect(component.accountForm.get('accountNumber')?.hasError('required')).toBeTruthy();
    expect(component.accountForm.get('balance')?.hasError('required')).toBeTruthy();
  });
});
