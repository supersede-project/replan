#!/bin/sh
bundle install
rake db:migrate
#rake db:seed
BUILD_ID=dontKillMe nohup rails server -d -b 217.194.3.60 -p 3000 &
