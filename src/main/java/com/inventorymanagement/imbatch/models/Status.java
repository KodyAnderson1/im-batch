package com.inventorymanagement.imbatch.models;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@Accessors(chain = true)
public class Status {

    private String status;
    private String message;
    private List<AdditionalStatus> additionalStatus;

}
