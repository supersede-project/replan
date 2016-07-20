# README

This is a mockup implementation of the Release Planner API for the UI

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
The last instrucction populates the DB wiht sample data. There is just one Project with id = 1.
## Heroku Version
This implementation has a heroku deployment that you can [test easily](http://editor.swagger.io/?import=https%3A%2F%2Fdl.dropboxusercontent.com%2Fu%2F219266%2Ft4_1_ui_api.yml). Note that there is just one Project, with id = 1.