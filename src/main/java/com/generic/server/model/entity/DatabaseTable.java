package com.generic.server.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A generic model class (DTO/Record) to hold the results of the table listing query. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "Represents a non-system table in the PostgreSQL database.",
    example =
        "{\"tableName\": \"users\", \"schemaName\": \"public\", \"tableOwner\": \"app_user\"}")
public class DatabaseTable {

  @Schema(description = "The name of the database table (e.g., 'orders').", example = "users")
  private String tableName;

  @Schema(
      description = "The schema containing the table (e.g., 'public' or 'analytics').",
      example = "public")
  private String schemaName;

  @Schema(
      description = "The PostgreSQL role that owns the table. Used for access control.",
      example = "app_service_account")
  private String tableOwner;
}
