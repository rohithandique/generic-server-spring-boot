package com.generic.server.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
public class KafkaConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.properties.schema.registry.url}")
  private String schemaRegistryUrl;

  private final KafkaProperties kafkaProperties;

  @Bean
  public ProducerFactory<String, SpecificRecordBase> producerFactory() {
    Map<String, Object> configProps = kafkaProperties.buildProducerProperties();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    configProps.put("schema.registry.url", schemaRegistryUrl);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  @Primary
  public KafkaTemplate<String, SpecificRecordBase> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    Map<String, Object> configProps = kafkaProperties.buildConsumerProperties();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
    configProps.put("schema.registry.url", schemaRegistryUrl);
    configProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
    return new DefaultKafkaConsumerFactory<>(configProps);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> genericRecordFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory
        .getConsumerFactory()
        .updateConfigs(Map.of(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false));
    return factory;
  }

  @Bean
  @ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
  public CommandLineRunner verifyKafkaConnection(KafkaAdmin kafkaAdmin) {
    return args -> {
      log.info("--- Starting Kafka Connectivity Test ---");
      try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
        DescribeClusterResult result = client.describeCluster();
        String clusterId = result.clusterId().get();
        int nodeCount = result.nodes().get().size();
        log.info("✅ SUCCESSFULLY connected to Aiven Kafka!");
        log.info("Cluster ID: {}", clusterId);
        log.info("Active Nodes: {}", nodeCount);
      } catch (Exception e) {
        log.error("❌ FAILED to connect to Aiven Kafka.");
        log.error("Error details: {}", e.getMessage());
        if (e.getCause() != null) {
          log.error("Root Cause: {}", e.getCause().getMessage());
        }
        throw new InterruptedException(e.getMessage());
      }
      log.info("--- Kafka Connectivity Test Finished ---");
    };
  }
}
