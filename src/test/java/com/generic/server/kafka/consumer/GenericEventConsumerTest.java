package com.generic.server.kafka.consumer;

import static org.mockito.Mockito.verify;

import com.enterprise.retail.v1.CustomerInfo;
import com.enterprise.retail.v1.OrderEvent;
import com.enterprise.retail.v1.OrderStatus;
import java.time.Instant;
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
  void listen() {
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
            .setItems(java.util.List.of("item1", "item2")) // Required
            .setTotalAmount(new java.math.BigDecimal("99.99")) // Required
            .setStatus(OrderStatus.PENDING)
            .build();
    ConsumerRecord<String, OrderEvent> testRecord =
        new ConsumerRecord<>("test-topic", 1, 0, "key", orderEvent);

    // When
    genericEventConsumer.listen(testRecord);

    // Then
    verify(genericEventConsumer).process(testRecord);
    verify(genericEventConsumer).handleMessage(orderEvent);
  }
}
