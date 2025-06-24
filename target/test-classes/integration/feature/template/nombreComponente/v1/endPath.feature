@cluster @nameSpace @componente @componente_endPath

Feature: feature de ejemplo

  Background:
    Given url host.connect
    * def body = read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/schemas/requests/apiBaasLbtr_body.json')
    * def schema = read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/schemas/responses/domesticPayments.json')

  @performance=componente @env=perf
  Scenario: Performance
    * def rut = karate.get('__gatling.RUT_CARGO','0965965408')
    * def accountId = karate.get('__gatling.CUENTA_ID','1255606')
    Given path 'api-baas-lbtr','v1', 'domestic-payment'
    And header x-channel = 'ConnectX'
    And set body.initiation.debtor_account.id = rut
    And set body.initiation.debtor_account.account_id = accountId
    When request body
    Then method POST
    And status 200


  @componente_endPath_200

  Scenario Outline:<idAzure> _ <testCase>
    Given path 'api-baas-lbtr','v1', 'domestic-payment'
    And headers <header>
    And set body.payment_id = <payment_id>
    And set body.description = <description>
    And set body.initiation.debtor_account.name = <debtor_account.name>
    And set body.initiation.debtor_account.id = <debtor_account.id>
    And set body.initiation.debtor_account.email = <debtor_account.email>
    And set body.initiation.debtor_account.account_id = <debtor_account.account_id>
    And set body.initiation.debtor_account.account_type = <debtor_account.account_type>
    And set body.initiation.debtor_account.bank_id = <debtor_account.bank_id>
    And set body.initiation.creditor_account.name = <creditor_account.name>
    And set body.initiation.creditor_account.id = <creditor_account.id>
    And set body.initiation.creditor_account.email = <creditor_account.email>
    And set body.initiation.creditor_account.account_id = <creditor_account.account_id>
    And set body.initiation.creditor_account.account_type = <creditor_account.account_type>
    And set body.initiation.creditor_account.bank_id = <creditor_account.bank_id>
    And set body.initiation.instructed_amount.amount = <instructed_amount.amount>
    And set body.initiation.instructed_amount.currency = <instructed_amount.currency>
    And set body.initiation.signature[0].id = <signature.id>
    And set body.initiation.signature[0].name = <signature.name>
    And set body.initiation.signature[0].authentication_type = <signature.authentication_type>
    And request body
    When method post
    Then status <status>
    And match response == schema

    Examples:
      | read('classpath:integration/feature/template/nombreComponente/v1/data/endPath200.json') |
