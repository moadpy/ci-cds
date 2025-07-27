package org.cicd.accountservice.controller;

import org.cicd.accountservice.dto.BankAccountDTO;
import org.cicd.accountservice.service.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")

@CrossOrigin(origins = "http://localhost:4200")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public List<BankAccountDTO> getAllAccounts() {
        return bankAccountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccountDTO> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getAccountById(id));
    }

    @PostMapping
    public BankAccountDTO createAccount(@RequestBody BankAccountDTO bankAccountDTO) {
        return bankAccountService.createAccount(bankAccountDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankAccountDTO> updateAccount(@PathVariable Long id, @RequestBody BankAccountDTO bankAccountDTO) {
        return ResponseEntity.ok(bankAccountService.updateAccount(id, bankAccountDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        bankAccountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
