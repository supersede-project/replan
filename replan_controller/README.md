# README

This the Replan controller component, this component provides several REST interfaces for the other components (and external WPs) to interact with the Replan tool.

## Installation
### Install Ruby on Rails v.5
```
#!bash
$ gem install rails -v 5.0.0
```
### Clone, install, and create & populate the DB
```
#!bash
$ git clone <this_repo>
$ cd <this_repo>
$ bundle install
$ rake db:migrate
$ rake db:seed
```
The last instruction populates the DB with sample data. There is just one Project with id = 1.

## API reference documents

[API to be consumed by the dashboard (UI)](https://supersede-project.github.io/replan/replan_controller/API-UI.html)

[API to be consumed by decision making (WP3)](https://supersede-project.github.io/replan/replan_controller/API-WP3.html)
