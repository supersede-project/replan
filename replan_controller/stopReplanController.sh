#!/bin/sh
port=$(lsof -i tcp:3000 -t) 
if [[ $port ]]; then
  echo 'Stopping Replan Service'
  kill -9 $port
fi
