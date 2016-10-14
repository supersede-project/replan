# Replan Optimizer

The optimizer has the sole purpose of generating a release plan. It has been developed as a stateless web service that given all the required information generates a release plan that preserving the stated constraints optimizes the use of the company resources to develop the next release. This web service is powered by the use of a problem optimization algorithm customized for this objective.

## Installation

### Compile instructions
To compile you only need to execute the gradle build script
```
gradlew build
```

### Install instructions
To deploy the service you only need to execute the jar file generated
```
java -jar /build/libs/optimizer-SNAPSHOT-1.0.jar
```

## API reference documents

[API to be consumed by the controller (CTL)](https://supersede-project.github.io/replan/replan_optimizer/API-CTL.html)

Contact: David Ameller <dameller@essi.upc.edu>

