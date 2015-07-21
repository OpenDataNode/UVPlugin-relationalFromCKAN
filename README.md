# E-RelationalFromCKAN #
----------

###General###

|                              |                                                               |
|------------------------------|---------------------------------------------------------------|
|**Name:**                     |E-RelationalFromCKAN                                           |
|**Description:**              |Downloads relational data from CKAN datastore resource.        |
|**Status:**                   |Supported in Plugins v2.1.X Updated to use Plugin-DevEnv v2.1.X |
|                              |                                                               |
|**DPU class name:**           |RelationalFromCKAN                                             | 
|**Configuration class name:** |RelationalFromCKANConfig_V1                                    |
|**Dialogue class name:**      |RelationalFromCKANVaadinDialog                                 | 

***

###Configuration parameters###

|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|org.opendatanode.CKAN.secret.token |Token used to authenticate to CKAN, has to be set in frontend.properties and backend.properties |
|org.opendatanode.CKAN.api.url      |URL to CKAN API internal_api, e.g. http://{host}/api/action/internal_api, has to be set in frontend.properties and backend.properties  |
|org.opendatanode.CKAN.http.header.[key] | Custom HTTP header added to requests on CKAN |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|output              |o          |RelationalDataUnit               |Downloaded relational data from CKAN datastore resource |


***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|1.0.0              |N/A                                             |
|1.0.1              |changed configuration parameter names and added one for http headers |
|1.1.0              |GUI changes, added new checkbox for showing only 'my' (organization) datasets, i18n update|
|                   |fixed bug: after selecting resource, it was not selected in tree after reopening config dialog|


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|mvi               |dependent on ckanext-odn-pipeline v0.5.1+| 

