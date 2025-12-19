package com.generic.server.kafka.consumer;

import com.enterprise.retail.v1.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GenericEventConsumer extends KafkaEventConsumer<IndexedRecord> {

  @Override
  public void handleMessage(IndexedRecord message) {
    if (message instanceof OrderEvent order) {
      log.info(
          "✅ Specific Record: Order {} for {}", order.getOrderId(), order.getCustomer().getEmail());
    } else if (message instanceof GenericRecord genericRecord) {
      String orderId = String.valueOf(genericRecord.get("orderId"));
      log.info("🚀 Generic Record: Order ID is {}", orderId);
    }
  }

  // Spring Kafka requires the listener method to be here
  @KafkaListener(
      topics = "${spring.kafka.generic-topic}",
      groupId = "${spring.kafka.consumer.group-id}-specific")
  public void listenWithClass(ConsumerRecord<String, IndexedRecord> kafkaRecord) {
    log.info("Received specific record: {}", kafkaRecord);
    super.process(kafkaRecord);
  }

  @KafkaListener(
      topics = "${spring.kafka.generic-topic}",
      groupId = "${spring.kafka.consumer.group-id}-generic",
      containerFactory = "genericRecordFactory")
  public void listenWithRegistry(ConsumerRecord<String, IndexedRecord> kafkaRecord) {
    log.info("Received generic record: {}", kafkaRecord);
    super.process(kafkaRecord);
  }
}
