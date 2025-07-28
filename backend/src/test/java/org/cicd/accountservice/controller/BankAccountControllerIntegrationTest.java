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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for BankAccountController using H2 in-memory database
 * This test verifies the complete flow from controller to database layer
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Transactional
public class BankAccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private ObjectMapper objectMapper;

    private BankAccountDTO testAccountDTO;

    @BeforeEach
    void setUp() {
        // Create a test account for integration tests
        BankAccountDTO newAccount = new BankAccountDTO();
        newAccount.setAccountNumber("INT123456");
        newAccount.setAccountHolderName("Integration Test User");
        newAccount.setBalance(new BigDecimal("1500.00"));
        testAccountDTO = bankAccountService.createAccount(newAccount);
    }

    @Test
    void shouldGetAllAccountsFromDatabase() throws Exception {
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].accountNumber").value("INT123456"))
                .andExpect(jsonPath("$[0].accountHolderName").value("Integration Test User"));
    }

    @Test
    void shouldCreateAccountInDatabase() throws Exception {
        BankAccountDTO newAccount = new BankAccountDTO();
        newAccount.setAccountNumber("INT789012");
        newAccount.setAccountHolderName("New Integration User");
        newAccount.setBalance(new BigDecimal("2000.00"));

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("INT789012"))
                .andExpect(jsonPath("$.accountHolderName").value("New Integration User"))
                .andExpect(jsonPath("$.balance").value(2000.00));
    }

    @Test
    void shouldUpdateAccountInDatabase() throws Exception {
        testAccountDTO.setAccountHolderName("Updated Integration User");
        testAccountDTO.setBalance(new BigDecimal("2500.00"));

        mockMvc.perform(put("/api/accounts/{id}", testAccountDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAccountDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountHolderName").value("Updated Integration User"))
                .andExpect(jsonPath("$.balance").value(2500.00));
    }

    @Test
    void shouldDeleteAccountFromDatabase() throws Exception {
        mockMvc.perform(delete("/api/accounts/{id}", testAccountDTO.getId()))
                .andExpect(status().isNoContent());

        // Verify the account is deleted
        mockMvc.perform(get("/api/accounts/{id}", testAccountDTO.getId()))
                .andExpect(status().isNotFound());
    }
}
