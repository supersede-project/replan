#!/bin/sh
kill -9 $(lsof -i tcp:3000 -t)
