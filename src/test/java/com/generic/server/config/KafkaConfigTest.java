package com.generic.server.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {

  @InjectMocks private KafkaConfig kafkaConfig;

  @Mock private KafkaProperties kafkaProperties;

  @Mock private KafkaAdmin kafkaAdmin;

  @Mock private AdminClient adminClient;

  @Mock private DescribeClusterResult describeClusterResult;

  @Mock private KafkaFuture<String> clusterIdFuture;

  @Mock private KafkaFuture<java.util.Collection<org.apache.kafka.common.Node>> nodesFuture;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "localhost:9092");
    ReflectionTestUtils.setField(kafkaConfig, "schemaRegistryUrl", "http://localhost:8081");
  }

  @Test
  void producerFactory() {
    when(kafkaProperties.buildProducerProperties()).thenReturn(new HashMap<>());
    assertNotNull(kafkaConfig.producerFactory());
  }

  @Test
  void kafkaTemplate() {
    when(kafkaProperties.buildProducerProperties()).thenReturn(new HashMap<>());
    assertNotNull(kafkaConfig.kafkaTemplate());
  }

  @Test
  void consumerFactory() {
    when(kafkaProperties.buildConsumerProperties()).thenReturn(new HashMap<>());
    assertNotNull(kafkaConfig.consumerFactory());
  }

  @Test
  void kafkaListenerContainerFactory() {
    when(kafkaProperties.buildConsumerProperties()).thenReturn(new HashMap<>());
    assertNotNull(kafkaConfig.kafkaListenerContainerFactory());
  }

  @Test
  void genericRecordFactory() {
    when(kafkaProperties.buildConsumerProperties()).thenReturn(new HashMap<>());
    assertNotNull(kafkaConfig.genericRecordFactory());
  }

  @Test
  void verifyKafkaConnection_success() throws Exception {
    Map<String, Object> config = new HashMap<>();
    when(kafkaAdmin.getConfigurationProperties()).thenReturn(config);

    try (var mockedAdminClient = mockStatic(AdminClient.class)) {
      mockedAdminClient.when(() -> AdminClient.create(config)).thenReturn(adminClient);
      when(adminClient.describeCluster()).thenReturn(describeClusterResult);
      when(describeClusterResult.clusterId()).thenReturn(clusterIdFuture);
      when(describeClusterResult.nodes()).thenReturn(nodesFuture);
      when(clusterIdFuture.get()).thenReturn("cluster-id");
      when(nodesFuture.get()).thenReturn(java.util.Collections.emptyList());

      kafkaConfig.verifyKafkaConnection(kafkaAdmin).run();

      mockedAdminClient.verify(() -> AdminClient.create(config));
    }
  }

  @Test
  void verifyKafkaConnection_failure() {
    Map<String, Object> config = new HashMap<>();
    when(kafkaAdmin.getConfigurationProperties()).thenReturn(config);

    try (var mockedAdminClient = mockStatic(AdminClient.class)) {
      mockedAdminClient.when(() -> AdminClient.create(config)).thenReturn(adminClient);
      when(adminClient.describeCluster()).thenThrow(new RuntimeException("Connection failed"));

      assertThrows(
          InterruptedException.class, () -> kafkaConfig.verifyKafkaConnection(kafkaAdmin).run());

      mockedAdminClient.verify(() -> AdminClient.create(config));
    }
  }

  @Test
  void verifyKafkaConnection_failure_withCause() {
    Map<String, Object> config = new HashMap<>();
    when(kafkaAdmin.getConfigurationProperties()).thenReturn(config);

    try (var mockedAdminClient = mockStatic(AdminClient.class)) {
      mockedAdminClient.when(() -> AdminClient.create(config)).thenReturn(adminClient);
      when(adminClient.describeCluster())
          .thenThrow(new RuntimeException("Connection failed", new Throwable("Root cause")));

      assertThrows(
          InterruptedException.class, () -> kafkaConfig.verifyKafkaConnection(kafkaAdmin).run());

      mockedAdminClient.verify(() -> AdminClient.create(config));
    }
  }
}
