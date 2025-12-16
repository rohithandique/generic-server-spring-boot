package com.generic.server.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SQLConfig {

  /**
   * Spring Boot automatically provides a configured DataSource (HikariCP) based on the
   * 'spring.datasource' properties. We mark it @Primary as this is the main RDBMS connection.
   */
  @Primary
  @Bean
  public JdbcTemplate postgresJdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
