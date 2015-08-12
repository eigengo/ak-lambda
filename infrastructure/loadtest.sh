#!/bin/sh

docker run eigrad/tcpkali \
--first-message 'device-id' \
--message 'device-data' \
--message-rate 10 \
--connections 10 \
192.168.59.103:32775
