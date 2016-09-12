!\bin\sh
bundle install
rake db:migrate
rake db:seed
rails server -d -b 192.168.145.13 -p 3000
