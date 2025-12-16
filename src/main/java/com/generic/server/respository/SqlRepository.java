package com.generic.server.respository;

import com.generic.server.model.entity.DatabaseTable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SqlRepository {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public SqlRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Enterprise ready: Uses a PreparedStatement internally via queryForList/query. The query is
   * defined as a constant for clarity and reusability.
   */
  private static final String FIND_USER_TABLES_SQL =
      """
        SELECT tablename, schemaname, tableowner
        FROM pg_catalog.pg_tables
        WHERE schemaname != 'pg_catalog'
        AND schemaname != 'information_schema'
        """;

  /** Executes the query and maps the result set rows to DatabaseTable model objects. */
  public List<DatabaseTable> findApplicationTables() {
    return jdbcTemplate.query(
        FIND_USER_TABLES_SQL,
        (rs, rowNum) ->
            DatabaseTable.builder()
                .tableName(rs.getString("tablename"))
                .schemaName(rs.getString("schemaname"))
                .tableOwner(rs.getString("tableowner"))
                .build());
  }
}
