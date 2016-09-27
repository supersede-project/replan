# Replan Dashboard
The main purpose of this component is to offer an easy to use user interface in order to facilitate the access to the main functionality of the tool. This component also allows to the end user to provide the additional information required by the tool (e.g., the resources available for each release). 

## Installation
The following steps describe the installation procedure for the Release planner dashboard

### Compile instructions

1. Clone the SUPERSEDE replan_dashboard Git repository. The directory where you extracted the distribution will be indicated in the following as `<dashboard_dir>`
1. If you cloned the Git repository, build the project: 
 * `cd <dashboard_dir>/release-planner-app/`
 * `./gradle build`

### Install instructions
1. Copy and rename the SUPERSEDE replan_dashboard war file you generated to their destination Tomcat directory:
 * `cp <dashboard_dir>/release-planner-app/build/libs/release-planner-app -0.0.1-SNAPSHOT.war.original <CATALINA_HOME>/webapps/ release-planner-app.war`
1. Run Tomcat from its bin directory. 
 * `cd <CATALINA_HOME>/bin/`
 * `./start_up.sh`

Contact: Danilo Valerio <danilo.valerio@siemens.com> 

