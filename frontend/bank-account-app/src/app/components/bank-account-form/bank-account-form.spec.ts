import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankAccountForm } from './bank-account-form';

describe('BankAccountForm', () => {
  let component: BankAccountForm;
  let fixture: ComponentFixture<BankAccountForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BankAccountForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BankAccountForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
