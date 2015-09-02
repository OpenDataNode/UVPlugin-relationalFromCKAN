### Description

Downloads relational data from CKAN datastore resource

### Configuration parameters

| Name | Description |
|:----|:----|
|Only my own datasets|If checked, only my organization's (public and private) datasets will be shown. Otherwise all public datasets of other organizations will be shown too.|
|Ignore Database column types (use Strings)|If checked, strings will be used as the type of the database collumns|
|Expand / Collapse all|Buttons that expand or collapse the loaded tree|
|Search dataset / relational resource|Filtering according to tree node names|
|Relational resource to download|Tree consisting from: Organization name (1st level), Dataset name (2nd level), Resource name (3rd level)|
|Rename table name to|Name of database table, which the data from CKAN will be loaded to. If no name is provided, resource ID will be used.|
### Inputs and outputs

|Name |Type | DataUnit | Description | Mandatory |
|:--------|:------:|:------:|:-------------|:---------------------:|
|output |o| FilesDataUnit | Downloaded relational data from CKAN datastore resource | |