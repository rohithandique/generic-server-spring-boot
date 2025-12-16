package com.generic.server.controller;

import com.generic.server.model.entity.DatabaseTable;
import com.generic.server.service.GenericService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Metadata Management",
    description = "API for retrieving application and infrastructure metadata.")
@RestController
@RequestMapping(value = "/api/v1/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
public class GenericController {

  private final GenericService genericService;

  @Autowired
  public GenericController(GenericService genericService) {
    this.genericService = genericService;
  }

  /**
   * Endpoint to retrieve all non-system tables from the PostgreSQL database. This method is
   * enhanced with OpenAPI annotations for Apigee consumption.
   */
  @Operation(
      summary = "Retrieve Application Database Tables",
      description =
          "Fetches a list of all non-system tables defined in the PostgreSQL database schema. Useful for monitoring/diagnostics.",
      operationId = "getApplicationTables")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved the list of database tables.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = DatabaseTable.class)))
  @ApiResponse(
      responseCode = "500",
      description = "Internal Server Error. Connection failed or query execution error.",
      content =
          @Content(
              schema =
                  @Schema(example = "{\"status\": 500, \"error\": \"Database connection error\"}")))
  @GetMapping("/tables")
  public ResponseEntity<List<DatabaseTable>> getAppTables() {
    List<DatabaseTable> tables = genericService.getApplicationTables();
    return ResponseEntity.ok().body(tables);
  }
}
