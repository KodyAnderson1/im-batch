package com.inventorymanagement.imbatch.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    List<String> roles = new ArrayList<>();
    roles.add("ROLE_USER");
    roles.add("ROLE_ADMIN");
    return User.builder()
            .username(username)
            .roles(roles.toArray(new String[0]))
            .password("password")
            .build();
  }
}
