package com.generic.server.kafka.consumer;

import static org.mockito.Mockito.verify;

import com.enterprise.retail.v1.CustomerInfo;
import com.enterprise.retail.v1.OrderEvent;
import com.enterprise.retail.v1.OrderStatus;
import java.time.Instant;
import org.apache.avro.generic.IndexedRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenericEventConsumerTest {

  @Spy @InjectMocks private GenericEventConsumer genericEventConsumer;

  @Test
  @SuppressWarnings("unchecked")
  void listenWithClass() {
    // Given
    OrderEvent orderEvent =
        OrderEvent.newBuilder()
            .setOrderId("123")
            .setTimestamp(Instant.now())
            .setCustomer(
                CustomerInfo.newBuilder()
                    .setEmail("test@example.com")
                    .setCustomerId("test-id")
                    .build())
            .setItems(java.util.List.of("item1", "item2"))
            .setTotalAmount(new java.math.BigDecimal("99.99"))
            .setStatus(OrderStatus.PENDING)
            .build();

    // Create the specific record
    ConsumerRecord<String, OrderEvent> specificRecord =
        new ConsumerRecord<>("test-topic", 1, 0, "key", orderEvent);

    // Cast to the generic type expected by the method signature: IndexedRecord
    ConsumerRecord<String, IndexedRecord> testRecord =
        (ConsumerRecord<String, IndexedRecord>) (ConsumerRecord<?, ?>) specificRecord;

    // When
    genericEventConsumer.listenWithClass(testRecord);

    // Then
    verify(genericEventConsumer).process(testRecord);
    verify(genericEventConsumer).handleMessage(orderEvent);
  }

  @Test
  void listenWithRegistry() {
    // Mock the Avro GenericRecord (The Registry Path)
    org.apache.avro.generic.GenericRecord mockRecord =
        org.mockito.Mockito.mock(org.apache.avro.generic.GenericRecord.class);
    org.mockito.Mockito.when(mockRecord.get("orderId")).thenReturn("456");

    ConsumerRecord<String, IndexedRecord> testRecord =
        new ConsumerRecord<>("test-topic", 1, 0, "key", mockRecord);

    genericEventConsumer.listenWithRegistry(testRecord);

    verify(genericEventConsumer).handleMessage(mockRecord);
  }
}
