Feature: Servicio Mascara
  Background: Config feature
  Given url host.msMascara510
  #* def rut = karate.get('__arg.rut', '22282646')

  @mascara_v5
  Scenario: Enmascarar Cuenta
    Given path 'mascara', 'v5'
    And request {"data":{"dato1": '#(rut)'}}
    When method Post
    Then status 200
    * def mascara = response.mascaras.dato1
