# Bank Account Service

**Note:** This is a simple Spring Boot application created for the purpose of demonstrating a CI/CD pipeline. The focus is on the pipeline itself, not the complexity of the application.

This is a simple RESTful web service for managing bank accounts. It is built with Spring Boot, Java 17, and Maven.

## Project Structure

The project is structured into the following packages:

*   `model`: Contains the JPA entity `BankAccount`.
*   `repository`: Contains the `BankAccountRepository` interface, which extends `JpaRepository` for database operations.
*   `service`: Contains the business logic for the application. `BankAccountService` is an interface that defines the service methods, and `BankAccountServiceImpl` is its implementation.
*   `controller`: Contains the `BankAccountController`, which exposes the REST API endpoints.

## API Endpoints

The following endpoints are available:

*   `GET /api/accounts`: Get all bank accounts.
*   `GET /api/accounts/{id}`: Get a bank account by its ID.
*   `POST /api/accounts`: Create a new bank account.
*   `PUT /api/accounts/{id}`: Update an existing bank account.
*   `DELETE /api/accounts/{id}`: Delete a bank account.

## Testing

The application includes both unit and integration tests. If you are new to testing, here is a brief overview.

### What are Tests in Software?

In software development, tests are small programs that check if your main application's code is working as you expect. We write code to test our code. This helps us catch bugs early, make changes with confidence, and ensure the application is reliable.

### Unit Tests

**What they are:** Unit tests are designed to check the smallest testable parts of an application in isolation. In our case, this means testing a single method within a class, like a method in our `BankAccountServiceImpl`.

**How they work:** When we unit test a piece of code, we want to isolate it from its dependencies. For example, our `BankAccountServiceImpl` depends on `BankAccountRepository` to talk to the database. In a unit test, we don't want to involve the real database. Instead, we use a "mock" or a "fake" version of the `BankAccountRepository`. We use a library called **Mockito** to create these mocks.

This allows us to ask questions like: "When I call the `createAccount` method in my service, does it correctly call the `save` method on the repository?" We can check this interaction without needing a database at all. This makes unit tests very fast and focused.

You can find our unit tests in `src/test/java/org/cicd/accountservice/service/BankAccountServiceImplTest.java`.

### Integration Tests

**What they are:** After testing the small parts in isolation, we need to check if they work together correctly. This is what integration tests are for. They test the integration between different layers of the application.

**How they work:** For our application, an integration test will check the complete flow from the API endpoint to the database. For example, it will simulate a real HTTP request to `POST /api/accounts` and verify that a new bank account is actually saved in the database and that the API returns the correct response.

For these tests, we use a real Spring Boot application context and a real (but temporary, in-memory) **H2 database**. This ensures that our controller, service, repository, and database configuration are all working together as expected. We use **MockMvc** to make the simulated HTTP requests.

You can find our integration tests in `src/test/java/org/cicd/accountservice/controller/BankAccountControllerTest.java`.

## Building and Running the Application

To build the application, run the following command:

```bash
mvn clean install
```

To run the application, run the following command:

```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`.

## Running the Tests

To run all the tests (both unit and integration), run the following command:

```bash
mvn test
```
