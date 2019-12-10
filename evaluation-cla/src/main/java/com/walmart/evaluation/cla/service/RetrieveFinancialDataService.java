package com.walmart.evaluation.cla.service;

import com.walmart.evaluation.cla.Constants;
import com.walmart.evaluation.dto.InventorySummary;
import com.walmart.evaluation.dto.UpcSummary;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/financial")
@Slf4j
public class RetrieveFinancialDataService {
  @Value("${ws.url.evaluation}")
  private String evaluationWsUrl;

  private final RestTemplate restTemplate;

  public RetrieveFinancialDataService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Get the financial details per upc.
   */
  @GetMapping("/details")
  public void getFinancialData() {
    String inventorySummaryWsUrl = evaluationWsUrl + "/inventory/summary";
    InventorySummary inventory = restTemplate
        .getForObject(inventorySummaryWsUrl, InventorySummary.class);

    String title = new StringBuilder(paddingRight(Constants.LABEL_UPC, 15))
        .append(paddingRight(Constants.LABEL_QUANTITY, 15))
        .append(paddingRight(Constants.LABEL_AMOUNT, 15)).toString();
    log.info(title);

    inventory.getUpcSummaryList().stream().forEach(x -> this.logLine(x));
  }

  private void logLine(UpcSummary upcSummary) {
    String line = new StringBuilder(paddingRight(upcSummary.getUpc(), 15))
        .append(paddingRight(upcSummary.getQuantity(), 15))
        .append(paddingRight(upcSummary.getTotalValue(), 15)).toString();
    log.info(line);
  }

  private String paddingRight(String str, int length) {
    StringBuilder sb = new StringBuilder(str);
    while (sb.length() < length) {
      sb.append(" ");
    }
    return sb.toString();
  }

  private String paddingRight(int value, int length) {
    StringBuilder sb = new StringBuilder(String.valueOf(value));
    while (sb.length() < length) {
      sb.append(" ");
    }
    return sb.toString();
  }

  private String paddingRight(BigDecimal value, int length) {
    StringBuilder sb = new StringBuilder(String.valueOf(value));
    while (sb.length() < length) {
      sb.append(" ");
    }
    return sb.toString();
  }

}
