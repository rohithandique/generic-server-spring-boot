package com.generic.server.controller;

import com.generic.server.model.entity.DatabaseTable;
import com.generic.server.service.GenericService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Generic Controller", description = "Generic API")
@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
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

  @Operation(
      summary = "Publish Generic Message",
      description =
          "Triggers a dummy order processing flow and returns the generated Order ID. This is typically used for integration testing or simulating message events.",
      operationId = "publishGenericMessage")
  @ApiResponse(
      responseCode = "200",
      description = "Message successfully triggered.",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema =
                  @Schema(
                      example = "{\"message\": \"Order message triggered!\", \"id\": \"12345\"}")))
  @ApiResponse(
      responseCode = "500",
      description = "Internal Server Error. Failed to trigger the message flow.",
      content =
          @Content(
              schema =
                  @Schema(
                      example = "{\"status\": 500, \"error\": \"Messaging service unavailable\"}")))
  @PostMapping("/publish-generic-message")
  public ResponseEntity<Map<String, String>> publishGenericMessage() {
    String orderId = genericService.processGenericMessage();
    return ResponseEntity.ok(Map.of("message", "Order message triggered!", "id", orderId));
  }
}
