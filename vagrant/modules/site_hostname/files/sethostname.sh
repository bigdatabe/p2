#!/bin/bash



ip=`ifconfig eth1 | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*'`
host="`echo $ip | sed 's/\./-/g'`.storm.nathan.gs"

echo $ip $host >> /etc/hosts
hostname $host