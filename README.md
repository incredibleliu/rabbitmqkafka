docker-machine start
docker run --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --publish 1414:1414 --publish 9443:9443 --detach ibmcom/mq
docker-machine ip

//kafka
cd E:\kafka\kafka_2.12-2.1.0\bin\windows
zookeeper-server-start.bat ..\..\config\zookeeper.properties
kafka-server-start.bat ..\..\config\server.properties
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic flink_output
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic flink_input
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic rabbitmqkafka
#kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic mqkafka



zookeeper-server-start ../../config/zookeeper.properties
kafka-server-start ../../config/server.properties
kafka-topics --zookeeper localhost:2181 --list


kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic rabbitmqkafka --from-beginning --max-messages 10

xml->avro
1. https://github.com/elodina/xml-avro
   SimpleConverter good, use this
2. https://www.confluent.io/blog/kafka-connect-single-message-transformation-tutorial-with-examples/
   NA
3. https://github.com/mit-ll/xml-avro-converter
   complicated, hard to implement