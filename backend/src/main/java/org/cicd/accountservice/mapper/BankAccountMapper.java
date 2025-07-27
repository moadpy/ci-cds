package org.cicd.accountservice.mapper;

import org.cicd.accountservice.dto.BankAccountDTO;
import org.cicd.accountservice.model.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    BankAccountMapper INSTANCE = Mappers.getMapper(BankAccountMapper.class);

    BankAccountDTO toDto(BankAccount bankAccount);

    BankAccount toEntity(BankAccountDTO bankAccountDTO);
}

