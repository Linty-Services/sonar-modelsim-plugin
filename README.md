# SonarQube Linty ModelSim Plugin

This plugin adds the ability to import ModelSim XML reports data into SonarQube.

## Usage

### Generating Coverage Report from ModelSim
Coverage report can be generated through the UI or command line.

#### UI
Go to **Tools > Coverage Report > Text**. The **XML format** box must be ticked.
**Code coverage > Statements**, and **Code coverage > Branches** or **Code coverage > Conditions** must be enabled
according to the type of coverage you'd like to import into SonarQube. **Condition/Expression tables** option is necessary
to import condition coverage. Do not activate other unnecessary options such as **Toggles**.

#### Command Line
Here's an example of command line:
```
coverage report -file report.xml -byfile -detail -all -dump -option -code {s b c} –xml
```

### Importing ModelSim Coverage Report into SonarQube
Add the following properties to your SonarQube analysis configuration:
* `sonar.modelsim.reportPaths`: Comma-separated list of paths (either files or directories) to ModelSim XML report files.
If the list contains a directory, all `.xml` files in this directory will be considered as ModelSim XML reports.
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

## Update All Dependencies
```bash
# Check for Maven dependencies to update
mvn org.codehaus.mojo:versions-maven-plugin:2.12.0:display-dependency-updates -Pits

# Check for Maven plugins to update
mvn org.codehaus.mojo:versions-maven-plugin:2.12.0:display-plugin-updates -Pits

# Check for versions in properties to update
mvn org.codehaus.mojo:versions-maven-plugin:2.12.0:display-property-updates -Pits

# Update parent POM
mvn org.codehaus.mojo:versions-maven-plugin:2.12.0:update-parent
```
