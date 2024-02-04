package com.inventorymanagement.imbatch.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
  private final String customMessage;
  private final String status;

  public BadRequestException(String customMessage, String status) {
    super(customMessage);
    this.customMessage = customMessage;
    this.status = status;
  }


  public BadRequestException() {
    super("Bad Request");
    this.customMessage = "Bad Request";
    this.status = "400";
  }

  public BadRequestException(String customMessage) {
    super(customMessage);
    this.customMessage = customMessage;
    this.status = "400";
  }


  public BadRequestException(String customMessage, Throwable cause) {
    super(customMessage, cause);
    this.customMessage = customMessage;
    this.status = "400";
  }

  public BadRequestException(Throwable cause) {
    super(cause);
    this.customMessage = "Bad Request";
    this.status = "400";
  }


}
