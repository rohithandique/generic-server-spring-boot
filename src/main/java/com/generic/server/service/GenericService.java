package com.generic.server.service;

import com.enterprise.retail.v1.CustomerInfo;
import com.enterprise.retail.v1.OrderEvent;
import com.enterprise.retail.v1.OrderStatus;
import com.generic.server.kafka.producer.KafkaEventProducer;
import com.generic.server.model.entity.DatabaseTable;
import com.generic.server.respository.SqlRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class GenericService {

  private final SqlRepository sqlRepository;
  private final KafkaEventProducer<OrderEvent> producer;

  @Value("${spring.kafka.generic-topic}")
  private String genericTopic;

  @Autowired
  public GenericService(SqlRepository sqlRepository, KafkaEventProducer<OrderEvent> producer) {
    this.sqlRepository = sqlRepository;
    this.producer = producer;
  }

  /**
   * Business method to fetch table list.
   *
   * @return List of application-defined tables.
   */
  public List<DatabaseTable> getApplicationTables() {
    return sqlRepository.findApplicationTables();
  }

  public String processGenericMessage() {
    // 1. Create the required nested customer record
    CustomerInfo customer =
        CustomerInfo.newBuilder().setCustomerId("CUST-123").setEmail("test@example.com").build();

    // 2. Build the OrderEvent with ALL required fields
    OrderEvent order =
        OrderEvent.newBuilder()
            .setOrderId(UUID.randomUUID().toString())
            .setTimestamp(Instant.now())
            .setCustomer(customer) // Required
            .setItems(java.util.List.of("item1", "item2")) // Required
            .setTotalAmount(new java.math.BigDecimal("99.99")) // Required
            .setStatus(OrderStatus.PENDING) // Required
            .build();

    producer.send(genericTopic, order.getOrderId(), order);
    return order.getOrderId();
  }
}
