package com.generic.server.respository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.generic.server.model.entity.DatabaseTable;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

class SqlRepositoryTest {

  @Mock private JdbcTemplate jdbcTemplate;

  @InjectMocks private SqlRepository sqlRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void findApplicationTables() {
    // Given
    when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<DatabaseTable>>any()))
        .thenAnswer(
            invocation -> {
              RowMapper<DatabaseTable> rowMapper = invocation.getArgument(1);
              ResultSet rs = mock(ResultSet.class);
              when(rs.getString("tablename")).thenReturn("test_table");
              when(rs.getString("schemaname")).thenReturn("test_schema");
              when(rs.getString("tableowner")).thenReturn("test_owner");
              return List.of(Objects.requireNonNull(rowMapper.mapRow(rs, 1)));
            });

    // When
    List<DatabaseTable> actualTables = sqlRepository.findApplicationTables();

    // Then
    assertEquals(1, actualTables.size());
    DatabaseTable table = actualTables.getFirst();
    assertEquals("test_table", table.getTableName());
    assertEquals("test_schema", table.getSchemaName());
    assertEquals("test_owner", table.getTableOwner());
  }
}
