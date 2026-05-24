/home/nathan/kafka_2.13-4.2.0/bin/kafka-topics.sh --bootstrap-server localhost:9092 \
  --create \
  --topic coinbase-trades \
  --partitions 3 \
  --replication-factor 1
