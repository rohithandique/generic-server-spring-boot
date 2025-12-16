package com.generic.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.generic.server.model.entity.DatabaseTable;
import com.generic.server.respository.SqlRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GenericServiceTest {

  @Mock private SqlRepository sqlRepository;

  @InjectMocks private GenericService genericService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getApplicationTables() {
    DatabaseTable databaseTable = new DatabaseTable("test_table", "public", "test_user");
    List<DatabaseTable> expectedTables = Collections.singletonList(databaseTable);
    when(sqlRepository.findApplicationTables()).thenReturn(expectedTables);

    List<DatabaseTable> actualTables = genericService.getApplicationTables();

    assertEquals(expectedTables, actualTables);
  }
}
