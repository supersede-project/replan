#!/bin/sh
echo "Installing Replan Controler"
bundle install
echo "Migrating the DB"
rake db:migrate
#rake db:seed
echo "Launching Replan Controller"
#BUILD_ID=dontKillMe nohup rails server -d -b 217.194.3.60 -p 3000 &
daemonize -E BUILD_ID=dontKillMe nohup rails server -d -b 217.194.3.60 -p 3000 &
