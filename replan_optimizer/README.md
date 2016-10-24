# Replan Optimizer

The optimizer has the sole purpose of generating a release plan. It has been developed as a stateless web service that given all the required information generates a release plan that preserving the stated constraints optimizes the use of the company resources to develop the next release. This web service is powered by the use of a problem optimization algorithm customized for this objective.

## Installation
The following steps describe the installation procedure for the Replan optimizer

### Compilation instructions

1. Clone the SUPERSEDE Replan Git repository.
 * `git clone https://github.com/supersede-project/replan`
1. Build the project.
 * `cd replan_optimizer`
 * `./gradlew build`

### Installation instructions
1. Copy the dashboard war file to the Tomcat directory.
 * `cp <generated WAR> <CATALINA_HOME>/webapps/`
1. Run Tomcat. 
 * `cd <CATALINA_HOME>/bin/`
 * `./start_up.sh`

## API reference documents

[API to be consumed by the controller (CTL)](https://supersede-project.github.io/replan/replan_optimizer/API-CTL.html)

Contact: David Ameller <dameller@essi.upc.edu>

