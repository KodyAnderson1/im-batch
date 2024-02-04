package com.inventorymanagement.imbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ImBatchApplication {

  public static void main(String[] args) {
    SpringApplication.run(ImBatchApplication.class, args);
  }

}
