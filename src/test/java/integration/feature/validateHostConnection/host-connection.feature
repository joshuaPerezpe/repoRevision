@validate_host_connection
Feature: Validar conexiones a los dns (hosts) del env.

    @dns_host.movaBice
    Scenario:<host.movaBice>
        * print host.movaBice
        Given url host.movaBice
        When method get
        Then status 404

    @dns_host.coreBice
    Scenario:<host.coreBice>
        * print host.coreBice
        Given url host.coreBice
        When method get
        Then status 404

    @dns_host.apigeeNet
    Scenario:<host.apigeeNet>
        * print host.apigeeNet
        Given url host.apigeeNet
        When method get
        Then status 404

    @dns_host.connect
    Scenario:<host.connect>
        * print host.connect
        Given url host.connect
        When method get
        Then status 404


