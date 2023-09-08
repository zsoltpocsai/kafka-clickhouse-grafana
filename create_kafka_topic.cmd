docker exec api-metrics-kafka-1 /opt/bitnami/kafka/bin/kafka-topics.sh ^
  --bootstrap-server localhost:9092 --create --topic %1