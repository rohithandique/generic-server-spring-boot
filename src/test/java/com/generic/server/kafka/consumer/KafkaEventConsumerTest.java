package com.generic.server.kafka.consumer;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaEventConsumerTest {

  @Spy
  private KafkaEventConsumer<SpecificRecordBase> kafkaEventConsumer =
      new KafkaEventConsumer<>() {
        @Override
        public void handleMessage(SpecificRecordBase message) {
          // Test implementation
        }
      };

  @Test
  void process_success() {
    // Given
    ConsumerRecord<String, SpecificRecordBase> testRecord =
        new ConsumerRecord<>("test-topic", 1, 0, "key", Mockito.mock(SpecificRecordBase.class));

    // When
    kafkaEventConsumer.process(testRecord);

    // Then
    verify(kafkaEventConsumer).handleMessage(testRecord.value());
  }

  @Test
  void process_failure() {
    // Given
    ConsumerRecord<String, SpecificRecordBase> testRecord =
        new ConsumerRecord<>("test-topic", 1, 0, "key", Mockito.mock(SpecificRecordBase.class));
    doThrow(new RuntimeException("Processing error"))
        .when(kafkaEventConsumer)
        .handleMessage(testRecord.value());

    // When
    kafkaEventConsumer.process(testRecord);

    // Then
    verify(kafkaEventConsumer).handleMessage(testRecord.value());
  }
}
