#!/bin/sh
/usr/local/rvm/gems/ruby-2.4.1/bin/bundle update
/usr/local/rvm/gems/ruby-2.4.1/bin/rake db:migrate
#rake db:seed
BUILD_ID=dontKillMe nohup rails server -d -b 192.168.145.13 -p 3000 &
