package com.generic.server.kafka.producer;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaEventProducer<T extends SpecificRecordBase> {

  private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

  public KafkaEventProducer(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  /**
   * Generic send method for any Avro SpecificRecord
   *
   * @param topic Target Kafka topic
   * @param key Message key (usually an ID)
   * @param data The Avro object
   */
  public void send(String topic, String key, T data) {
    log.info("Producing message to topic: {} | Type: {}", topic, data.getClass().getSimpleName());

    CompletableFuture<SendResult<String, SpecificRecordBase>> future =
        kafkaTemplate.send(topic, key, data);

    future.whenComplete(
        (result, ex) -> {
          if (ex == null) {
            log.info(
                "✅ Sent message to [{}] partition: {}",
                topic,
                result.getRecordMetadata().partition());
          } else {
            log.error("❌ Failed to send message to [{}] due to: {}", topic, ex.getMessage());
          }
        });
  }
}
