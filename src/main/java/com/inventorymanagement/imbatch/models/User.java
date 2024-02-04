package com.inventorymanagement.imbatch.models;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class User {
  private final String clerkId;
  private final String firstName;
  private final String lastName;
  private final String username;
  private final String imageUrl;
  private final List<String> roles;
  //  private final Map<String, String> userOrganizations;
}
