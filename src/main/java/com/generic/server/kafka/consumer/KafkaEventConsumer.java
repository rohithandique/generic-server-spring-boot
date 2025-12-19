package com.generic.server.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.IndexedRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Slf4j
public abstract class KafkaEventConsumer<T extends IndexedRecord> {

  /** Template method to be implemented by specific consumers */
  public abstract void handleMessage(T message);

  protected void process(ConsumerRecord<String, T> kafkaRecord) {
    T message = kafkaRecord.value();
    log.info(
        "📥 Received message from topic: {} | Partition: {} | Offset: {}",
        kafkaRecord.topic(),
        kafkaRecord.partition(),
        kafkaRecord.offset());

    try {
      handleMessage(message);
    } catch (Exception e) {
      log.error("🛑 Error processing message: {}", e.getMessage(), e);
      // In enterprise apps, you'd trigger a Dead Letter Topic (DLT) here
    }
  }
}
