package org.cicd.accountservice.repository;

import org.cicd.accountservice.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}

