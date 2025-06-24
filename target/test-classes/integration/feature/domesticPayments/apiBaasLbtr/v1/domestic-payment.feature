@hbe @domesticPayments @api_baas_lbtr @api_baas_lbtr_domestic-payment
Feature: Api Baas Tef Lbtr

  Background:
    Given url host.connect
    * def body = read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/schemas/requests/apiBaasLbtr_body.json')
    * def body2 = read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/schemas/requests/apiBaasLbtr_body2.json')
    * def consultaOperProg = read('classpath:integration/helpers/DB/flowDB/apiBaasLbtr/apiBaasLbtr.feature@tbl_oper_prog_consulta')
    * def consultaDetalleCamp = read('classpath:integration/helpers/DB/flowDB/apiBaasLbtr/apiBaasLbtr.feature@tbl_detalle_camp_consulta')
    * def schema = read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/schemas/responses/domesticPayments.json')
    * configure readTimeout = 700000

  @performance=api_baas_lbtr @env=perf
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


  @api-baas-lbtr_domestic-payment_200
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
      | read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/data/domesticPayment200.json') |

  @ignore @api-baas-lbtr_domestic-payment_prueba_poderes @parallel=false
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
    And set body.initiation.signature[1].id = <signature.id2>
    And set body.initiation.signature[1].name = <signature.name2>
    And set body.initiation.signature[1].authentication_type = <signature.authentication_type2>
    And request body
    When method post
    Then status <status>
    * def numOperProg = response.transaction_id
    * def queryOperProg = call consultaOperProg {'operprog': '#(numOperProg)'}
    And match queryOperProg.tbl_oper_prog[0].COD_ESTADO == <numCodEstado>
    And match queryOperProg.tbl_oper_prog[0].NOM_CLIENTE == <debtor_account.name>
    And match queryOperProg.tbl_oper_prog[0].RUT_REGISTRO == '<rutRegistro>'
    And match queryOperProg.tbl_oper_prog[0].NOM_REGISTRO == '<nomRegistro>'
    * def queryDetalleCamp = call consultaDetalleCamp {'operprog': '#(numOperProg)'}
    And match queryDetalleCamp.tbl_detalle_camp[0].COD_CAMPO == 2
    And match queryDetalleCamp.tbl_detalle_camp[0].VAL_CAMPO contains 'N'
    And match queryDetalleCamp.tbl_detalle_camp[1].COD_CAMPO == 4
    And match queryDetalleCamp.tbl_detalle_camp[1].VAL_CAMPO == '00000000000' + <debtor_account.account_id>
    And match queryDetalleCamp.tbl_detalle_camp[2].COD_CAMPO == 5
    And match queryDetalleCamp.tbl_detalle_camp[2].VAL_CAMPO == <creditor_account.account_id>
    And match queryDetalleCamp.tbl_detalle_camp[3].COD_CAMPO == 6
    And match queryDetalleCamp.tbl_detalle_camp[3].VAL_CAMPO == '0'
    And match queryDetalleCamp.tbl_detalle_camp[4].COD_CAMPO == 7
    And match queryDetalleCamp.tbl_detalle_camp[4].VAL_CAMPO == '<instructed_amount.amount>'
    And match queryDetalleCamp.tbl_detalle_camp[6].COD_CAMPO == 19
    And match queryDetalleCamp.tbl_detalle_camp[6].VAL_CAMPO == <creditor_account.id>
    And match queryDetalleCamp.tbl_detalle_camp[7].COD_CAMPO == 21
    And match queryDetalleCamp.tbl_detalle_camp[7].VAL_CAMPO == '00100'
    And match queryDetalleCamp.tbl_detalle_camp[8].COD_CAMPO == 30
    And match queryDetalleCamp.tbl_detalle_camp[8].VAL_CAMPO == '00100'
    And match queryDetalleCamp.tbl_detalle_camp[19].COD_CAMPO == 518
    And match queryDetalleCamp.tbl_detalle_camp[19].VAL_CAMPO contains <description>
    And match queryDetalleCamp.tbl_detalle_camp[20].COD_CAMPO == 527
    And match queryDetalleCamp.tbl_detalle_camp[20].VAL_CAMPO == <creditor_account.name>
    And match queryDetalleCamp.tbl_detalle_camp[21].COD_CAMPO == 529
    And match queryDetalleCamp.tbl_detalle_camp[21].VAL_CAMPO == <creditor_account.bank_id>
    And match queryDetalleCamp.tbl_detalle_camp[23].COD_CAMPO == 539
    And match queryDetalleCamp.tbl_detalle_camp[23].VAL_CAMPO == <debtor_account.bank_id>
    And match queryDetalleCamp.tbl_detalle_camp[24].COD_CAMPO == 548
    And match queryDetalleCamp.tbl_detalle_camp[24].VAL_CAMPO == <debtor_account.id>
    And match queryDetalleCamp.tbl_detalle_camp[29].COD_CAMPO == 632
    And match queryDetalleCamp.tbl_detalle_camp[29].VAL_CAMPO == <debtor_account.email>
    And match queryDetalleCamp.tbl_detalle_camp[30].COD_CAMPO == 633
    And match queryDetalleCamp.tbl_detalle_camp[30].VAL_CAMPO == <debtor_account.name>
    And match queryDetalleCamp.tbl_detalle_camp[31].COD_CAMPO == 634
    And match queryDetalleCamp.tbl_detalle_camp[31].VAL_CAMPO == <creditor_account.email>
    And match queryDetalleCamp.tbl_detalle_camp[33].COD_CAMPO == 636
    And match queryDetalleCamp.tbl_detalle_camp[33].VAL_CAMPO == 'Simple'
    And match queryDetalleCamp.tbl_detalle_camp[49].COD_CAMPO == 800
    And match queryDetalleCamp.tbl_detalle_camp[49].VAL_CAMPO == '<x-channel>'
    And match queryDetalleCamp.tbl_detalle_camp[50].COD_CAMPO == 827
    And match queryDetalleCamp.tbl_detalle_camp[50].VAL_CAMPO == <payment_id>

    Examples:
      | read('classpath:integration/feature/domesticPayments/apiBaasLbtr/data/v1/domesticPaymentPoderesTc.json') |


  @api-baas-lbtr_domestic-payment400 @parallel=false
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
    Then status 400
    And match response.status == '<statusResponse>'
    And match response.data[0].message contains '<messageResponse>'

    Examples:
      | read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/data/domesticPayment400.json') |

  @api-baas-lbtr_domestic-payment_remove_400
  Scenario Outline:<idAzure> _ <testCase>
    Given path 'api-baas-lbtr','v1', 'domestic-payment'
    And headers <header>
    * remove body.<remove>
    And request body
    When method post
    Then status <status>
    And match response.status == '<statusResponse>'
    And match response.data[0].message == '<messageResponse>'

    Examples:
      | read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/data/domesticPaymentRemoveTc.json') |


  @api-baas-lbtr_domestic-payment_remove_200 @SanityServices
  Scenario Outline:<idAzure> _ <testCase>
    Given path 'api-baas-lbtr','v1', 'domestic-payment'
    And headers <header>
    * remove body.<remove>
    And request body
    When method post
    Then status <status>

    Examples:
      | read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/data/domesticPaymentRemove200Tc.json') |


  @api-baas-lbtr_domestic-payment_json
  Scenario Outline:<idAzure> _ <testCase>
    Given path 'api-baas-lbtr','v1', 'domestic-payment'
    And headers <header>
    And request body2
    When method post
    Then status <status>
    And match response.status == '<statusResponse>'
    And match response.data[0].message == '<messageResponse>'

    Examples:
      | read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/data/domesticPaymentJsonTc.json') |


  @api-baas-lbtr_domestic-payment_endpoint
  Scenario Outline:<idAzure> _ <testCase>
    Given path 'api-baas-lbtr','v1', 'domestic-payment', 'prueba'
    And headers <header>
    And request body
    When method post
    Then status <status>
    And match response.status == '<statusResponse>'
    And match response.data[0].message == '<messageResponse>'

    Examples:
      | read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/data/domesticPaymentEndpointTc.json') |


  @api-baas-lbtr_domestic-payment_method
  Scenario Outline:<idAzure> _ <testCase>
    Given path 'api-baas-lbtr','v1', 'domestic-payment'
    And headers <header>
    And request body
    When method <method>
    Then status <status>
    And match response.status == '<statusResponse>'
    And match response.data[0].message contains '<messageResponse>'

    Examples:
      | read('classpath:integration/feature/domesticPayments/apiBaasLbtr/v1/data/domesticPaymentMethodTc.json') |
