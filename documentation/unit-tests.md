# Unit Test Documentation

This document provides a detailed explanation of the unit tests for the `BankAccountServiceImpl` class. These tests are crucial for ensuring that the business logic of the service layer works correctly in isolation.

## Testing Framework and Tools*

-   **JUnit 5:** The primary testing framework used to write and run the tests.
-   **Mockito:** A mocking framework used to create mock objects of dependencies (like `BankAccountRepository` and `BankAccountMapper`). This allows us to test the service logic without needing a real database or a real mapper implementation.

## Test Class: `BankAccountServiceImplTest`

This class contains all the unit tests for `BankAccountServiceImpl`.

### Setup (`@BeforeEach`)

Before each test runs, the `setUp()` method is executed. It initializes a `BankAccount` entity and a corresponding `BankAccountDTO` object with sample data. These objects are used as standard test data across multiple test cases.

### Test Cases

Here is a breakdown of each test method:

#### 1. `getAllAccounts()`

-   **Purpose:** To verify that the `getAllAccounts` method correctly retrieves all bank accounts and maps them to DTOs.
-   **How it works:**
    1.  It configures the mock `bankAccountRepository` to return a list containing our sample `BankAccount` when `findAll()` is called.
    2.  It tells the mock `bankAccountMapper` to return our sample `BankAccountDTO` when `toDto()` is called.
    3.  It calls `bankAccountService.getAllAccounts()`.
    4.  It asserts that the returned list is not empty and contains one element.
    5.  It verifies that `findAll()` on the repository and `toDto()` on the mapper were each called exactly once.

#### 2. `getAccountById()`

-   **Purpose:** To ensure that a single bank account can be retrieved by its ID.
-   **How it works:**
    1.  The mock repository's `findById()` method is configured to return our sample `BankAccount` wrapped in an `Optional`.
    2.  The mock mapper is configured to return the sample DTO.
    3.  It calls `bankAccountService.getAccountById(1L)`.
    4.  It asserts that the returned DTO is not null and has the correct account number.
    5.  It verifies that `findById()` was called once.

#### 3. `getAccountById_whenNotFound_shouldThrowException()`

-   **Purpose:** To test that a `BankAccountNotFoundException` is thrown when trying to retrieve an account with an ID that does not exist.
-   **How it works:**
    1.  The mock repository's `findById()` is configured to return an empty `Optional`.
    2.  It calls `bankAccountService.getAccountById(1L)` and asserts that this call throws a `BankAccountNotFoundException`.

#### 4. `createAccount()`

-   **Purpose:** To verify that a new bank account can be created successfully.
-   **How it works:**
    1.  It configures the mock mapper's `toEntity()` to convert the sample DTO to an entity.
    2.  It configures the mock repository's `save()` method to return the sample entity.
    3.  It configures the mock mapper's `toDto()` to convert the saved entity back to a DTO.
    4.  It calls `bankAccountService.createAccount()` with the sample DTO.
    5.  It asserts that the returned DTO is not null and has the correct account number.
    6.  It verifies that the `save()` method on the repository was called once.

#### 5. `updateAccount()`

-   **Purpose:** To test the logic for updating an existing bank account.
-   **How it works:**
    1.  It creates a new DTO with updated details.
    2.  It configures the mock repository's `findById()` to return the original sample account.
    3.  It configures the `save()` method to return the (updated) account.
    4.  It configures the mock mapper to return the DTO with the updated details.
    5.  It calls `bankAccountService.updateAccount()` with the ID and the updated DTO.
    6.  It asserts that the returned DTO is not null and contains the updated account number.
    7.  It verifies that both `findById()` and `save()` were called once.

#### 6. `deleteAccount()`

-   **Purpose:** To ensure that an existing bank account can be deleted.
-   **How it works:**
    1.  It configures the mock repository's `existsById()` to return `true`, indicating the account exists.
    2.  It configures the `deleteById()` method to do nothing (as it returns `void`).
    3.  It calls `bankAccountService.deleteAccount(1L)`.
    4.  It verifies that `deleteById()` was called exactly once.

#### 7. `deleteAccount_whenNotFound_shouldThrowException()`

-   **Purpose:** To test that a `BankAccountNotFoundException` is thrown when trying to delete an account that does not exist.
-   **How it works:**
    1.  The mock repository's `existsById()` is configured to return `false`.
    2.  It calls `bankAccountService.deleteAccount(1L)` and asserts that this call throws a `BankAccountNotFoundException`.

