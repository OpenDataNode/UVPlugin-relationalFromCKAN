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
|dpu.uv-e-relationalFromCKAN.secret.token |authentication token |
|dpu.uv-e-relationalFromCKAN.catalog.api.url |URL to CKAN API internal_api, e.g. http://host/api/action/internal_api  |

***

### Inputs and outputs ###

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|output              |o          |RelationalDataUnit               |Downloaded relational data from CKAN datastore resource |


***

### Version history ###

|Version            |Release notes                                   |
|-------------------|------------------------------------------------|
|2.1.0              |N/A                                             |                                


***

### Developer's notes ###

|Author            |Notes                 |
|------------------|----------------------|
|mvi               |The configuration parameters are needed in both frontend and backend configuration files. |
|mvi               |dependent on ckanext-odn-pipeline branch feature/edem|  

