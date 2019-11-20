# Sonar-coverage-modelsim plugin for Sonarqube & Modelsim
===============

## Feature

This plugin allows to import Modelsim xml reports data in Sonarqube.

## Usage

### Compilation process

This plugin can be built with the mvn clean package install command.
 
### Generating coverage report
Coverage reports must be generated in Modelsim with Tools->Coverage Report->Text. The "XML format" box must be ticked. Code coverage->Statements and Code coverage->Branches or Code coverage->Conditions must be enabled according to what type of coverage needs to be imported. "Condition/Expression tables" option is necessary for importing condition coverage.
Activating unnecessary options (i.e. creating a report with both branch and condition coverage) will not prevent code coverage to be correctly imported.

### Importing coverage report
The report path can be set in Administration->Configuration->General Settings->Code Coverage->Modelsim tab in Sonarqube interface. Statement coverage will always be imported if available, while either branch or condition coverage can be imported by setting the "Coverage type" option. The results will be shown in the project's coverage tab in Sonarqube.

## License  
Copyright 2018-2019 Linty Services    
Licensed under the [GNU Lesser General Public License, Version 3.0](https://www.gnu.org/licenses/lgpl.txt)
