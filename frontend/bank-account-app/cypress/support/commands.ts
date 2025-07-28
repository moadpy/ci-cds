// ***********************************************
// This example commands.ts shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

declare global {
  namespace Cypress {
    interface Chainable {
      /**
       * Custom command to create a bank account via API
       * @example cy.createBankAccount('John Doe', '123456789', 1000)
       */
      createBankAccount(name: string, accountNumber: string, balance: number): Chainable<any>

      /**
       * Custom command to clean up test data
       * @example cy.cleanupTestData()
       */
      cleanupTestData(): Chainable<any>
    }
  }
}

Cypress.Commands.add('createBankAccount', (name: string, accountNumber: string, balance: number) => {
  cy.request({
    method: 'POST',
    url: `${Cypress.env('apiUrl')}/bank-accounts`,
    body: {
      accountHolderName: name,
      accountNumber: accountNumber,
      balance: balance
    }
  })
})

Cypress.Commands.add('cleanupTestData', () => {
  cy.request({
    method: 'GET',
    url: `${Cypress.env('apiUrl')}/bank-accounts`,
    failOnStatusCode: false
  }).then((response) => {
    if (response.status === 200 && response.body.length > 0) {
      response.body.forEach((account: any) => {
        cy.request({
          method: 'DELETE',
          url: `${Cypress.env('apiUrl')}/bank-accounts/${account.id}`,
          failOnStatusCode: false
        })
      })
    }
  })
})

export {}
