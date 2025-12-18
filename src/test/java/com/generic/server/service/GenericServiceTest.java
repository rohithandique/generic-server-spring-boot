package com.generic.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.enterprise.retail.v1.OrderEvent;
import com.generic.server.kafka.producer.KafkaEventProducer;
import com.generic.server.model.entity.DatabaseTable;
import com.generic.server.respository.SqlRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class GenericServiceTest {

  @Mock private SqlRepository sqlRepository;

  @Mock private KafkaEventProducer<OrderEvent> kafkaEventProducer;

  @InjectMocks private GenericService genericService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(genericService, "genericTopic", "test-topic");
  }

  @Test
  void getApplicationTables() {
    DatabaseTable databaseTable = new DatabaseTable("test_table", "public", "test_user");
    List<DatabaseTable> expectedTables = Collections.singletonList(databaseTable);
    when(sqlRepository.findApplicationTables()).thenReturn(expectedTables);

    List<DatabaseTable> actualTables = genericService.getApplicationTables();

    assertEquals(expectedTables, actualTables);
  }

  @Test
  void processGenericMessage() {
    doNothing().when(kafkaEventProducer).send(anyString(), anyString(), any(OrderEvent.class));

    String orderId = genericService.processGenericMessage();

    assertNotNull(orderId);
    verify(kafkaEventProducer).send(anyString(), anyString(), any(OrderEvent.class));
  }
}
