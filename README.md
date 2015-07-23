E-RelationalFromCKAN
----------

### Documentation

Downloads relational data from CKAN datastore resource.

### Configuration parameters

|Parameter                        |Description                             |                                                        
|---------------------------------|----------------------------------------|
|org.opendatanode.CKAN.secret.token  |authentication token |
|org.opendatanode.CKAN.api.url       |URL to CKAN API internal_api, e.g. http://host/api/action/internal_api  |


### Inputs and outputs

|Name                |Type       |DataUnit                         |Description                        |
|--------------------|-----------|---------------------------------|-----------------------------------|
|output              |o          |RelationalDataUnit               |Downloaded relational data from CKAN datastore resource |


### Version history

#### v1.0.0
* Initial version


### Developer's notes

The configuration parameters are needed in both frontend and backend configuration files, dependent on ckanext-odn-pipeline branch feature/edem
