# TEMPLATE_SERVICIOS
![Badge en Desarollo](https://img.shields.io/badge/STATUS-EN%20DESAROLLO-green)

- Se soluciona problema switch de environment


## MEJORAS v3.0.1

- Centralización de Data Provider.
- Se actualiza feature de ejemplo.
- Se acrualiza a karate 1.4.1.
- Se agrega config necesaria para Scala.
- Se agregan nuevos workflows para creacion, publicación y performance.
- Se actualiza el comando de ejecucion de performance ( Ver mas abajo).
- Se agrega ultima version del core al 06/12/2014 ( V4.0.1).
- Se agrega integración con libreria Azure_integration ( V1.1.7).
- Se actualiza integrationTest, CustomListener con el fin de soportar la integración con Azure Devops.
- Se actualiza el comando de ejecución para la creación de casos de prueba en Azure Devops ( Ver mas abajo).
- Se finaliza el soporte a Jira.
- Se agrega workflow para automatización de PR ( beta).


# CATALOGO

| SERVICIO                                                                                                                               | TAG FEATURE REGRESSION                                                 | PATH PERFORMANCE SIMULATION | # Casos Automatizados | # Suit ID |
|----------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------|-----------------------------|-----------------------|----------|
| [domestic-payment.feature](src%2Ftest%2Fjava%2Fintegration%2Ffeature%2FdomesticPayments%2FapiBaasLbtr%2Fv1%2Fdomestic-payment.feature) | @hbe @domesticPayments @api_baas_lbtr @api_baas_lbtr_domestic-payment  | apiBaasLbtr.DomesticPayment | 191                   | 123123   |
| [usersConfig.feature](src%2Ftest%2Fjava%2Fintegration%2Ffeature%2FbulkPayments%2FmsUsers%2Fv1%2FusersConfig.feature)                   |@biceConnect @bulkPayments @msUsers @msUsers_config| msUsers.usersConfig         | 3                     | 131713         |

## INSTALACIÓN

Se necesita tener instalado y [configurado](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/9860/Manual-B%C3%A1sico-Instalaciones-para-Uso-de-Framework-de-Automatizaci%C3%B3n-Skip-to-end-of-metadata) JAVA (11, soporta hasta 17)  y Maven (3.8.1)

```java

# paso 1 Descargar proyecto

git clone git@github.com:Bice-QA-Automatizacion/template_services.git

# paso 2 Instalar dependencias

mvn install -DskipTest=true

```

## VARIABLES DE ENTORNO

En el caso de ejecutar desde un IDE como IntelliJ IDEA, se debe configurar las siguientes variables de entorno en la configuración de ejecución.
En caso de ejecutar por CMD  declararlas en el ambiente local.

- ENV_EXECUTION= local (local, vm)
- [CLIENT_SECRET](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/13340/Habilitar-usuario-en-Azure-Devops) = Cliente secret de Azure Devops ( obtenido a partir del registro ).
- [ADO_REFRESH_TOKEN](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/13340/Habilitar-usuario-en-Azure-Devops) = Ado resfresh token ( obtenido a partir del registro ).
- PROJECT = Projecto en Azure Devops.
- USER_EMAIL = Email utilizado en para logearse el Azure Devops.

## EJECUCIÓN DE PRUEBAS

(*) Representa obligatoriedad.
<hr></hr>

### **LOCAL**

#### PARAMETRIZACIÓN

- TAG= El tag o selector de la **Feature** a ejecutar *.
- AUTOMATIONNAME = Nombre con el cual se agruparan los resultados en Azure Devops ( testPlan/runner).
- PLANID = Id del plan en Azure Devops. ( Es obligatorio, solo si la opcion **AZURE** esta en true )
- SUITEID = Id de la suite en Azure Devops.( Es obligatorio, solo si la opcion **AZURE** esta en true )
- AZURE = Publicar resultados en Azure Devops ( true, false por default).
- ENV = Entorno de ejecucion ( dev, qa, perf).
- CREATE = Crear casos de prueba en Azure Devops ( true, false por default. Solo requerido para creación).

##### PUBLICACIÓN DE RESULTADOS EN AZURE DEVOPS.

Para la publicación es necesario que los scenarios y features cumplan con el formato propuesto.
el siguiente [enlace](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/12432/Remando-hacia-Azure-Devops-creaci%C3%B3n-y-ejecuci%C3%B3n?anchor=ejecuci%C3%B3n) te direccionara al detalle.
```bash
mvn clean test "-Dkarate.options=--tags @{{TAG_AUTOMATION}}" -DplanId=@{{PLAN_ID_ADO}} -DsuiteId=@{{SUITE_ID_ADO}} -DautomationName=@{{RUNNER_NAME}} -Dazure=@{{PUBLISH?}} -Ddriver=karate -Dkarate.env=@{{ENVIRONMENT}}
```


#### CREACIÓN DE CASOS DE PRUEBA EN AZURE DEVOPS.

Para la creación de casos de prueba en Azure Devops. Es necesario respetar la estructura prupuesta.
En el siguiente [enlace](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/12432/Remando-hacia-Azure-Devops-creaci%C3%B3n-y-ejecuci%C3%B3n?anchor=creaci%C3%B3n-de-casos-de-pruebas-/-agrupaci%C3%B3n-por-suite) puedes
acceder a la informacion disponible imagenes/video.

```bash
mvn clean test "-Dkarate.options=--tags @{{TAG_AUTOMATION}}" -DplanId=@{{PLAN_ID_ADO}} -DsuiteId=@{{SUITE_ID_ADO}} -DsuiteName=@{{RUNNER_NAME}} -Dcreate=@{{CREATE?}} -Ddriver=karate -Dkarate.env=@{{ENVIRONMENT}}
```
#### REPORTERIA

Una vez concluida la ejecución de las pruebas, se generará un reporte en la ruta ____target/karate-reports/karate-summary.html____.

![img.png](img.png)

<hr></hr>

### GITHUB

Para ejecutar desde este ambiente, es necesario contar con una cuenta en GitHub. Ademas de el acceso
al repositorio que necesitas ejecutar.

Primero necesitas acceder al portal https://github.com/enterprises/banco-bice/ -> https://github.com/Bice-QA-Automatizacion

Luego ingresar al repositorio que necesitas ejecutar.

![img_1.png](img_1.png)

#### PARAMETRIZACIÓN

- TAG= El tag o selector de la **Feature** a ejecutar *.
- AUTOMATIONNAME = Nombre con el cual se agruparan los resultados en Azure Devops ( testPlan/runner).
- PLANID = Id del plan en Azure Devops. ( Es obligatorio, solo si la opcion **AZURE** esta en true )
- SUITEID = Id de la suite en Azure Devops.( Es obligatorio, solo si la opcion **AZURE** esta en true )
- AZURE = Publicar resultados en Azure Devops ( true, false por default).
- ENV = Entorno de ejecucion ( dev, qa, perf).
- CREATE = Crear casos de prueba en Azure Devops ( true, false por default. Solo requerido para creación).



##### PUBLICACIÓN DE RESULTADOS EN AZURE DEVOPS.
Luego debes continuar los siguientes [pasos](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/10072/Publicar-resultados-en-Azure-Devops-local).


##### CREACIÓN DE CASOS DE PRUEBA EN AZURE DEVOPS, POR MEDIO DE WORKFLOW.

Este flujo aun se encuentra en etapa de construcción.

#### REPORTERIA

Una vez concluida la ejecución de las pruebas, puedes acceder al reporte generado en GitHub, desde el enlace.



<hr></hr>


## Comando para crear casos de prueba en Azure Devops

```bash

 mvn clean test -DplanId=48213 -DparentSuite=48214 "-Dkarate.options=--tags @api_cuentas_200" -Dcreate=true -DsuiteName=api_cuentas_200 -Ddriver=karate
```




## Comando para publicar resultados en Azure Devops

```bash

mvn clean test "-Dkarate.options=--tags @api_cuentas_200" -DautomationName=api_cuentas_200 -DplanId=48213 -DsuiteId=65127 -Dazure=true -Ddriver=karate -Dkarate.env=qa

```

### Desactivar la llamada al Data Provider
(Si no se suministra el parametro en **false** llama al Data Provider)

```bash
-Dkarate.DProvider=false 
```

### Entorno de ejecución
```bash
-Dkarate.env=qa || dev || perf
```

### Para correr gatling

```bash
mvn clean test-compile -Dgatling.simulationClass=simulations.${{GATLING_CLASS_SIMULATION}} gatling:test
```

## Ejemplos
En la ruta features/examples. Encontraras ejemplos de pruebas a servicios con Karate

1. example_1: Validación de schemas.
2. example_2: Llamada a un servicio con una pre feature.
3. example_3: Prueba con archivo de datos.

## Como contribuir

1. Clona el repositorio y crea un nuevo branch: ```$ git checkout -b nuevo_branch```
2. Haz cambios y prueba
3. Envie una solicitud de extracción con una descripción completa con los cambios.

## Soporte

Para soporte: cristopher.gonzalez@bice.cl y jsandoval@bice.cl

## Documentos de ayuda

* [Agregar certificado Azure Devops](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/10070/Agregar-certificados-HTTPS)
* [Ambientes de ejecución](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/9501/GITHUB)
* [Proceso de automatización servicios](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/9328/03-Inducci%C3%B3n)
* [Habilitación en ADO](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/13340/Habilitar-usuario-en-Azure-Devops)
* [Proceso de publicación](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/10072/Publicar-resultados-en-Azure-Devops-local)
* [Servicios Mock](https://dev.azure.com/BiceCl/Gesti%C3%B3n%20de%20Proyectos%20DIVOT/_wiki/wikis/Gesti%C3%B3n-de-Proyectos-DIVOT.wiki/13401/Creaci%C3%B3n-de-nuevos-endpoints)

