package com.generic.server.service;

import com.generic.server.model.entity.DatabaseTable;
import com.generic.server.respository.SqlRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenericService {

  private final SqlRepository sqlRepository;

  @Autowired
  public GenericService(SqlRepository sqlRepository) {
    this.sqlRepository = sqlRepository;
  }

  /**
   * Business method to fetch table list. Future enterprise logic (e.g., caching the result in Redis
   * or merging with MongoDB collection info) would go here.
   *
   * @return List of application-defined tables.
   */
  public List<DatabaseTable> getApplicationTables() {
    return sqlRepository.findApplicationTables();
  }
}
