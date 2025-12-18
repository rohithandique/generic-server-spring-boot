package com.generic.server.kafka.consumer;

import com.enterprise.retail.v1.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GenericEventConsumer extends KafkaEventConsumer<OrderEvent> {

  @Override
  //    @KafkaListener(topics = "${spring.kafka.generic-topic}", groupId =
  // "${spring.kafka.consumer.group-id}")
  public void handleMessage(OrderEvent order) {
    log.info(
        "🚀 Business Logic: Processing Order {} for customer {}",
        order.getOrderId(),
        order.getCustomer().getEmail());
  }

  // Spring Kafka requires the listener method to be here
  @KafkaListener(
      topics = "${spring.kafka.generic-topic}",
      groupId = "${spring.kafka.consumer.group-id}")
  public void listen(ConsumerRecord<String, OrderEvent> kafkaRecord) {
    super.process(kafkaRecord);
  }
}
