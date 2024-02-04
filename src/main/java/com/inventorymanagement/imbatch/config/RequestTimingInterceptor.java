package com.inventorymanagement.imbatch.config;

import com.inventorymanagement.imbatch.utilities.ConsoleFormatter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.inventorymanagement.imbatch.utilities.ConsoleFormatter.printColored;
import static java.lang.String.format;

@Slf4j
@Component
public class RequestTimingInterceptor implements HandlerInterceptor {



  @Override
  public boolean preHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler) {

    long startTime = System.currentTimeMillis();
    request.setAttribute("startTime", startTime);
    return true;
  }

  @Override
  public void afterCompletion(@NonNull HttpServletRequest request,
                              @NonNull HttpServletResponse response,
                              @NonNull Object handler,
                              Exception ex) {

    long startTime = (Long) request.getAttribute("startTime");
    long endTime = System.currentTimeMillis();
    long executeTime = endTime - startTime;

    String path = request.getRequestURI();
    String method = request.getMethod();
    String query = request.getQueryString();

    if (query != null) {
      printColored(format("%d ms - %s %s with query %s.", executeTime, method, path, query), ConsoleFormatter.Color.GREEN);
    } else {
      printColored(format("%d ms - %s %s.", executeTime, method, path), ConsoleFormatter.Color.GREEN);
    }
  }
}
