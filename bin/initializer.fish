#!/usr/bin/env fish

# to start the broker in standalone mode
set KAFKA_CLUSTER_ID "$(bin/kafka-storage.sh random-uuid)"
bin/kafka-storage.sh format --standalone -t $KAFKA_CLUSTER_ID -c config/server.properties
bin/kafka-server-start.sh config/server.properties