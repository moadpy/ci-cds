package org.cicd.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cicd.accountservice.dto.BankAccountDTO;
import org.cicd.accountservice.service.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private ObjectMapper objectMapper;

    private BankAccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        BankAccountDTO newAccount = new BankAccountDTO();
        newAccount.setAccountNumber("98765");
        newAccount.setAccountHolderName("Integration Test Holder");
        newAccount.setBalance(new BigDecimal("5000.00"));
        accountDTO = bankAccountService.createAccount(newAccount);
    }

    @Test
    void getAllAccounts() throws Exception {
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("98765"));
    }

    @Test
    void getAccountById() throws Exception {
        mockMvc.perform(get("/api/accounts/" + accountDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("98765"));
    }

    @Test
    void getAccountById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/accounts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAccount() throws Exception {
        BankAccountDTO newAccount = new BankAccountDTO();
        newAccount.setAccountNumber("11223");
        newAccount.setAccountHolderName("New Holder");
        newAccount.setBalance(new BigDecimal("300.00"));

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("11223"));
    }

    @Test
    void updateAccount() throws Exception {
        BankAccountDTO updatedDetails = new BankAccountDTO();
        updatedDetails.setAccountNumber("55555");
        updatedDetails.setAccountHolderName("Updated Holder");
        updatedDetails.setBalance(new BigDecimal("9999.00"));

        mockMvc.perform(put("/api/accounts/" + accountDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("55555"));
    }

    @Test
    void deleteAccount() throws Exception {
        mockMvc.perform(delete("/api/accounts/" + accountDTO.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAccount_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/accounts/999"))
                .andExpect(status().isNotFound());
    }
}
