@biceConnect @bulkPayments @msUsers @msUsers_config

Feature: Servicio Mapeo Usuarios

  Background:
    Given url host.connect
    * def schema = read('classpath:integration/feature/bulkPayments/msUsers/v1/schemas/responses/usersConfig.json')


  @performance=msUsers @env=perf
  Scenario: Performance
    Given path 'ms-users','v1','v1','users', 'config'
    When method GET
    And status 200

  @msUsers_config_200
  Scenario Outline: <idTestJira> _ <testCase>
    Given path 'ms-users','v1','v1','users', 'config'
    When method GET
    And status 200
    And match response[0] == schema

    Examples:
      | read('classpath:integration/feature/bulkPayments/msUsers/v1/data/usersConfig200.json') |


  @msUsers_config_404
  Scenario Outline: <idTestJira> _ <testCase>
    Given path 'ms-users','v1','v1','users','<pruebaUri>','config'
    When method GET
    And status 404
    And match response.error contains '<mensaje>'

    Examples:
      | read('classpath:integration/feature/bulkPayments/msUsers/v1/data/usersConfig404.json') |


  @msUsers_config_405
  Scenario Outline: <idTestJira> _ <testCase>
    Given path 'ms-users','v1','v1','users', 'config'
    When method POST
    And status 405
    And match response.error contains '<mensaje>'

    Examples:
      | read('classpath:integration/feature/bulkPayments/msUsers/v1/data/usersConfig405.json') |



