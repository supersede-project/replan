# Replan Dashboard
The main purpose of this component is to offer an easy to use user interface in order to facilitate the access to the main functionality of the tool. This component also allows to the end user to provide the additional information required by the tool (e.g., the resources available for each release). 

## Installation
The following steps describe the installation procedure for the Replan dashboard

### Compile instructions

1. Clone the SUPERSEDE Replan Git repository.
 * `git clone https://github.com/supersede-project/replan`
1. Build the project.
 * `cd replan_dashboard/release-planner-app/`
 * `./gradlew build`

*Note: in step 2, you will need credentials to access to some of the dependencies specified in the Gradle project. Please contact with the SUPERSEDE Technical Integrator for more details: <jesus.gorronogoitia@atos.net>*

### Install instructions
1. Copy the dashboard war file to the Tomcat directory.
 * `cp <generated WAR> <CATALINA_HOME>/webapps/`
1. Run Tomcat. 
 * `cd <CATALINA_HOME>/bin/`
 * `./start_up.sh`

Contact: Danilo Valerio <danilo.valerio@siemens.com> 

