#!/usr/bin/env sh
set -eu

KAFKA_HOME="${KAFKA_HOME:-/home/nathan/coinbasekafkaservice}"
KAFKA_CONFIG="${KAFKA_CONFIG:-$KAFKA_HOME/config/server.properties}"

log_dirs=`sed -n 's/^[[:space:]]*log\.dirs[[:space:]]*=[[:space:]]*//p' "$KAFKA_CONFIG" | tail -1`

if [ -z "$log_dirs" ]; then
  echo "No log.dirs setting found in $KAFKA_CONFIG" >&2
  exit 1
fi

old_ifs="$IFS"
IFS=","
for log_dir in $log_dirs; do
  IFS="$old_ifs"
  log_dir=`printf '%s' "$log_dir" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'`
  if [ -r "$log_dir/meta.properties" ]; then
    exit 0
  fi
  IFS=","
done
IFS="$old_ifs"

cluster_id="${KAFKA_CLUSTER_ID:-`"$KAFKA_HOME/bin/kafka-storage.sh" random-uuid`}"
echo "Formatting Kafka KRaft storage for cluster id $cluster_id"
"$KAFKA_HOME/bin/kafka-storage.sh" format -t "$cluster_id" -c "$KAFKA_CONFIG" --standalone --ignore-formatted
