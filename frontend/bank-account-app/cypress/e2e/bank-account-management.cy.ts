describe('Bank Account Management E2E Tests', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('should display the bank account application', () => {
    cy.contains('Bank Account Management')
    cy.get('[data-cy=account-list]').should('exist')
    cy.get('[data-cy=account-form]').should('exist')
  })

  it('should create a new bank account', () => {
    // Fill out the form
    cy.get('[data-cy=account-holder-name]').type('John Doe')
    cy.get('[data-cy=account-number]').type('123456789')
    cy.get('[data-cy=balance]').type('1000')

    // Submit the form
    cy.get('[data-cy=submit-button]').click()

    // Verify the account appears in the list
    cy.get('[data-cy=account-list]').should('contain', 'John Doe')
    cy.get('[data-cy=account-list]').should('contain', '123456789')
    cy.get('[data-cy=account-list]').should('contain', '1000')
  })

  it('should edit an existing bank account', () => {
    // Create a test account first
    cy.createBankAccount('Jane Smith', '987654321', 2000)
    cy.reload()

    // Click edit button
    cy.get('[data-cy=edit-button]').first().click()

    // Update the account
    cy.get('[data-cy=account-holder-name]').clear().type('Jane Updated')
    cy.get('[data-cy=balance]').clear().type('2500')
    cy.get('[data-cy=submit-button]').click()

    // Verify the update
    cy.get('[data-cy=account-list]').should('contain', 'Jane Updated')
    cy.get('[data-cy=account-list]').should('contain', '2500')
  })

  it('should delete a bank account', () => {
    // Create a test account first
    cy.createBankAccount('Test User', '111222333', 500)
    cy.reload()

    // Delete the account
    cy.get('[data-cy=delete-button]').first().click()
    cy.get('[data-cy=confirm-delete]').click()

    // Verify the account is removed
    cy.get('[data-cy=account-list]').should('not.contain', 'Test User')
  })

  it('should validate form fields', () => {
    // Try to submit empty form
    cy.get('[data-cy=submit-button]').click()

    // Check for validation messages
    cy.get('[data-cy=account-holder-name-error]').should('be.visible')
    cy.get('[data-cy=account-number-error]').should('be.visible')
    cy.get('[data-cy=balance-error]').should('be.visible')
  })

  it('should handle API errors gracefully', () => {
    // Intercept API call to simulate error
    cy.intercept('POST', '**/api/bank-accounts', {
      statusCode: 500,
      body: { error: 'Internal Server Error' }
    }).as('createAccountError')

    // Fill form and submit
    cy.get('[data-cy=account-holder-name]').type('Error Test')
    cy.get('[data-cy=account-number]').type('999999999')
    cy.get('[data-cy=balance]').type('100')
    cy.get('[data-cy=submit-button]').click()

    // Verify error handling
    cy.wait('@createAccountError')
    cy.get('[data-cy=error-message]').should('be.visible')
    cy.get('[data-cy=error-message]').should('contain', 'Error creating account')
  })

  it('should display loading states', () => {
    // Intercept API call with delay
    cy.intercept('GET', '**/api/bank-accounts', (req) => {
      req.reply((res) => {
        res.delay(2000)
        res.send([])
      })
    }).as('getAccounts')

    cy.visit('/')

    // Check for loading indicator
    cy.get('[data-cy=loading-spinner]').should('be.visible')
    cy.wait('@getAccounts')
    cy.get('[data-cy=loading-spinner]').should('not.exist')
  })
})
