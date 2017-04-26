<link rel="shortcut icon" type="image/png" href="images/favicon.png">
# Replan tool of [![SUPERSEDE](images/SUPERSEDE-logo.png)](https://www.supersede.eu/) project

The main functionality of the Replan tool is to give support to the software release planning. To provide this functionality the tool uses problem optimization algorithms.

Replan tool is intended for a context in which software projects normally involve several developers (could be many), developed in multiple iterations and/or releases, with a plenty of requirements to be implemented. The complexity of managing the resources is very high even in small groups of developers because we have to consider, among other things, all the dependencies between the requirements and the adequacy of each developer to perform a particular task. 

Replan tool is **not** intended to substitute the project leader/manager, but to give support. In this sense, the tool will provide one complete release planning based on the information provided which then can be accepted (or not) by the project leader/manager. 

[![Test Replan!](images/testbtn.png)](http://supersede.es.atos.net:8080/)

User/password: Please request by email to David Ameller <dameller@essi.upc.edu>

Replan tool is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Replan tool is composed by three components:

- *Replan Dashboard*: UI / front end ([README.md](https://github.com/supersede-project/replan/blob/master/replan_dashboard/README.md))
- *Replan Controller*: Access point the components that communicate with the Replan tool ([README.md](https://github.com/supersede-project/replan/blob/master/replan_controller/README.md))
- *Replan Optimizer*: Main functionality of the tool, to provide an optimized solution to the release problem ([README.md](https://github.com/supersede-project/replan/blob/master/replan_optimizer/README.md))

*Please check the respective README.md for details.*

Main contact: David Ameller <dameller@essi.upc.edu>

![Project funded by the European Union](images/european.union.logo.png)
