import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BankAccountList } from './bank-account-list';

describe('BankAccountList', () => {
  let component: BankAccountList;
  let fixture: ComponentFixture<BankAccountList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BankAccountList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BankAccountList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
