package com.generic.server.kafka.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
class KafkaEventProducerTest {

  @InjectMocks private KafkaEventProducer<SpecificRecordBase> kafkaEventProducer;

  @Mock private KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

  @Mock private SendResult<String, SpecificRecordBase> sendResult;

  @Test
  void send_success() {
    // Given
    String topic = "test-topic";
    String key = "test-key";
    SpecificRecordBase data = mock(SpecificRecordBase.class);
    CompletableFuture<SendResult<String, SpecificRecordBase>> future = new CompletableFuture<>();
    RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(topic, 1), 0, 0, 0, 0, 0);

    when(kafkaTemplate.send(anyString(), anyString(), any(SpecificRecordBase.class)))
        .thenReturn(future);
    when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);

    // When
    kafkaEventProducer.send(topic, key, data);
    future.complete(sendResult);

    // Then
    verify(kafkaTemplate).send(topic, key, data);
  }

  @Test
  void send_failure() {
    // Given
    String topic = "test-topic";
    String key = "test-key";
    SpecificRecordBase data = mock(SpecificRecordBase.class);
    CompletableFuture<SendResult<String, SpecificRecordBase>> future = new CompletableFuture<>();

    when(kafkaTemplate.send(anyString(), anyString(), any(SpecificRecordBase.class)))
        .thenReturn(future);

    // When
    kafkaEventProducer.send(topic, key, data);
    future.completeExceptionally(new RuntimeException("Kafka is down"));

    // Then
    verify(kafkaTemplate).send(topic, key, data);
  }
}
