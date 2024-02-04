package com.inventorymanagement.imbatch.services.batch;

import org.springframework.context.ApplicationEvent;

public class BatchProcessEvent extends ApplicationEvent {
  public BatchProcessEvent(Object source) {
    super(source);
  }
  // Additional fields and methods can be added to pass information with the event
}
