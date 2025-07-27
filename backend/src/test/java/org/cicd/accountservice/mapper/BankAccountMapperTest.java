package org.cicd.accountservice.mapper;

import org.cicd.accountservice.dto.BankAccountDTO;
import org.cicd.accountservice.model.BankAccount;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankAccountMapperTest {

    private final BankAccountMapper mapper = Mappers.getMapper(BankAccountMapper.class);

    @Test
    void shouldMapBankAccountToDto() {
        // given
        BankAccount entity = new BankAccount(1L, "12345", "Test Holder", new BigDecimal("1000.00"));

        // when
        BankAccountDTO dto = mapper.toDto(entity);

        // then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getAccountNumber(), dto.getAccountNumber());
        assertEquals(entity.getAccountHolderName(), dto.getAccountHolderName());
        assertEquals(entity.getBalance(), dto.getBalance());
    }

    @Test
    void shouldMapDtoToBankAccount() {
        // given
        BankAccountDTO dto = new BankAccountDTO();
        dto.setId(1L);
        dto.setAccountNumber("12345");
        dto.setAccountHolderName("Test Holder");
        dto.setBalance(new BigDecimal("1000.00"));

        // when
        BankAccount entity = mapper.toEntity(dto);

        // then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getAccountNumber(), entity.getAccountNumber());
        assertEquals(dto.getAccountHolderName(), entity.getAccountHolderName());
        assertEquals(dto.getBalance(), entity.getBalance());
    }

    @Test
    void shouldReturnNullDtoWhenBankAccountIsNull() {
        // when
        BankAccountDTO dto = mapper.toDto(null);

        // then
        assertNull(dto);
    }

    @Test
    void shouldReturnNullEntityWhenDtoIsNull() {
        // when
        BankAccount entity = mapper.toEntity(null);

        // then
        assertNull(entity);
    }
}

