package com.walmart.evaluation.cla.service;

import com.walmart.evaluation.cla.Constants;
import com.walmart.evaluation.cla.utils.ClaUtils;
import com.walmart.evaluation.dto.InventoryAdjustment;
import com.walmart.evaluation.dto.InventoryItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class UpdateInventoryService {

  @Value("${ws.url.inventory}")
  private String inventoryWsUrl;

  private final RestTemplate restTemplate;

  public UpdateInventoryService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Update the inventory quantity by upc.
   *
   * @param args the input from cla
   * @return InventoryItem the updated inventory information
   */
  public InventoryItem adjustInventory(String... args) {


    InventoryAdjustment adjustment = buildAdjustment(args);

    if (adjustment == null) {
      log.error("The input is invalid, please change it and try again.");
      return null;
    }
    InventoryItem inventoryItem = this.adjustInventory(adjustment);

    log.info("Information is updated successfully.");
    return inventoryItem;
  }

  /**
   * Adjust the amount of item by upc.
   *
   * @param adjustment inventory values to adjust
   * @return InventoryItem
   */
  public InventoryItem adjustInventory(InventoryAdjustment adjustment) {
    String endpointUrl = inventoryWsUrl + "/inventory/adjustments";
    InventoryItem inventoryItem = this.restTemplate
        .postForObject(endpointUrl, adjustment, InventoryItem.class);
    return inventoryItem;
  }

  private InventoryAdjustment buildAdjustment(String... args) {
    if (args.length < 3 || !ClaUtils.isNumber(args[1]) || !ClaUtils.isNumber(args[2])) {
      return null;
    }
    int sign = 1;
    if (Constants.SALE.equalsIgnoreCase(args[0])) {
      sign = -1;
    }
    return new InventoryAdjustment(Integer.parseInt(args[1]), Integer.parseInt(args[2]) * sign);
  }

}
