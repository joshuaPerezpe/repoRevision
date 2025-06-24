Feature: Genera TOKEN edgemicro-auth
  Background: Config feature
    Given url host.apigeeNet
    #* def rut = karate.get('__arg.rut', '22282646')

  @get_token
  Scenario: Genera Token
    Given path 'edgemicro-auth','token'
    And header Content-Type = 'application/json'
    And header Authorization = basicAuth
    And header x-rut-cliente = rut
    And request {"grant_type": "client_credentials"}
    When method post
    Then status 200
