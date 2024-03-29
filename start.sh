#!/bin/bash

# Start the first process
nginx&
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start nginx: $status"
  exit $status
fi

# Start the second process
java -jar -Dspring.profiles.active=default /app.jar&
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start bot: $status"
  exit $status
fi

while sleep 60; do
  echo -n ""
done

# Naive check runs checks once a minute to see if either of the processes exited.
# This illustrates part of the heavy lifting you need to do if you want to run
# more than one service in a container. The container exits with an error
# if it detects that either of the processes has exited.
# Otherwise it loops forever, waking up every 60 seconds

#while sleep 60; do
#  ps aux |grep nginx |grep -q -v grep
#  PROCESS_1_STATUS=$?
#  ps aux |grep java |grep -q -v grep
#  PROCESS_2_STATUS=$?
#  # If the greps above find anything, they exit with 0 status
#  # If they are not both 0, then something is wrong
#  if [ $PROCESS_1_STATUS -ne 0]; then
#    echo "Nginx has exited."
#    exit 1
#  fi
#  if [ $PROCESS_2_STATUS -ne 0]; then
#    echo "Bot has exited."
#    exit 1
#  fi
#done
