#!/bin/sh
#bundle update
#rake db:migrate
#rake db:seed
BUILD_ID=dontKillMe nohup rails server -d -b 192.168.145.13 -p 3000 &
