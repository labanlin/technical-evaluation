package com.walmart.evaluation.cla.utils;

public class ClaUtils {
  public static boolean isNumber(String str) {
    return str != null && str.matches("-?\\d+(\\.\\d+)?");
  }
}
