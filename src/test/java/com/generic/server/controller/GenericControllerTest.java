package com.generic.server.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.generic.server.model.entity.DatabaseTable;
import com.generic.server.service.GenericService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GenericController.class)
class GenericControllerTest {

  @TestConfiguration
  static class ControllerTestConfig {
    @Bean
    public GenericService genericService() {
      return Mockito.mock(GenericService.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private GenericService genericService;

  @Test
  @WithMockUser
  void getAppTables() throws Exception {
    DatabaseTable table1 = new DatabaseTable();
    table1.setTableName("TABLE1");
    DatabaseTable table2 = new DatabaseTable();
    table2.setTableName("TABLE2");
    List<DatabaseTable> tables = Arrays.asList(table1, table2);

    when(genericService.getApplicationTables()).thenReturn(tables);

    mockMvc
        .perform(get("/api/v1/metadata/tables").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].tableName").value("TABLE1"))
        .andExpect(jsonPath("$[1].tableName").value("TABLE2"));
  }
}
