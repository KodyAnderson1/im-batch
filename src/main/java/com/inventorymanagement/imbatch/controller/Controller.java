package com.inventorymanagement.imbatch.controller;

import com.inventorymanagement.imbatch.services.batch.BatchProcessEvent;
import com.inventorymanagement.imbatch.utilities.UpdateOnDays;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Controller", description = "API")
@RequestMapping(value = "/", produces = "application/json", consumes = "application/json")
public class Controller {

  private final ApplicationEventPublisher eventPublisher;

  @GetMapping("/trigger")
  public ResponseEntity<String> triggerBatchProcess() {
    eventPublisher.publishEvent(new BatchProcessEvent(this));
    return new ResponseEntity<>("Batch Process Triggered", HttpStatus.OK);
  }

  @GetMapping("/test2/{day}") // TODO: Implement
  public ResponseEntity<String> test2(@PathVariable UpdateOnDays day) {
    return new ResponseEntity<>(day.name(), HttpStatus.NOT_IMPLEMENTED);
  }

}
