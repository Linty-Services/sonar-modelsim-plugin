# SonarQube ModelSim Plugin

This plugin adds the ability to import ModelSim XML reports data into SonarQube.

## Usage

### Generating Coverage Report from ModelSim
Coverage reports must be generated in ModelSim with **Tools > Coverage Report > Text**. The **XML format** box must be ticked.
**Code coverage > Statements**, and **Code coverage > Branches** or **Code coverage > Conditions** must be enabled
according to the type of coverage you'd like to import into SonarQube. **Condition/Expression tables** option is necessary
to import condition coverage. Activating unnecessary options (i.e. creating a report with both branch and condition coverage)
will not prevent code coverage to be correctly imported.

### Importing ModelSim Coverage Report into SonarQube
Add the following properties to your SonarQube analysis configuration:
* `sonar.modelsim.reportPath`: Path (absolute or relative) to ModelSim XML report file. Default value is `report.xml`.
* `sonar.modelsim.reportMode`: Type of secondary coverage: `branch` or `condition` (branch coverage will still be 
reported as condition coverage on the Sonarqube web interface). Statement coverage is always imported.
Default value is `branch`.


## Build Plugin

Without integration tests:
```
mvn clean package
```

With integration tests on SonarQube 7.9.4 version:
```
mvn clean verify -Pits -Dsonar.runtimeVersion=7.9.4
```
