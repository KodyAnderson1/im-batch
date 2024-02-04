package com.inventorymanagement.imbatch.utilities;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ConsoleFormatter {

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BOLD = "\u001B[1m";

  public static Map<Color, String> colorMap = Map.of(
          Color.BLACK, "\u001B[30m",
          Color.RED, "\u001B[31m",
          Color.GREEN, "\u001B[32m",
          Color.YELLOW, "\u001B[33m",
          Color.BLUE, "\u001B[34m",
          Color.PURPLE, "\u001B[35m",
          Color.CYAN, "\u001B[36m",
          Color.WHITE, "\u001B[37m"
  );

  public enum Color {
    BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, WHITE
  }

  public static void printColored(String text, Color color) {
    String colorCode = getColorCode(color);
    log.info(colorCode + ANSI_BOLD + text + ANSI_RESET);
  }

  public static void printColored(String text, Color color, boolean isBold) {
    String colorCode = getColorCode(color);
    String boldCode = isBold ? ANSI_BOLD : "";
    log.info(colorCode + boldCode + text + ANSI_RESET);
  }

  private static String getColorCode(Color color) {
    return colorMap.get(color);
  }

}