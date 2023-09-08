CREATE TABLE api_request (
  accountId UInt32,
  path String
)
ENGINE = MergeTree
ORDER BY (accountId, path);

CREATE TABLE api_request_queue (
  accountId UInt32,
  path String
)
ENGINE = Kafka
SETTINGS 
  kafka_broker_list = 'kafka:9092',
  kafka_topic_list = 'requests',
  kafka_group_name = 'clickhouse_group',
  kafka_format = 'JSONEachRow';

CREATE MATERIALIZED VIEW api_request_mv TO api_request AS 
  SELECT * FROM api_request_queue;
