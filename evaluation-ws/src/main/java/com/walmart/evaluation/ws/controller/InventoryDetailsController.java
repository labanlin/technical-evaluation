package com.walmart.evaluation.ws.controller;

import com.walmart.evaluation.dto.InventoryItem;
import com.walmart.evaluation.dto.InventorySummary;
import com.walmart.evaluation.ws.service.InventoryService;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/inventory")
public class InventoryDetailsController {
  private final InventoryService inventoryService;

  public InventoryDetailsController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  @GetMapping
  List<InventoryItem> getInventory() {
    return this.inventoryService.getInventory();
  }

  @GetMapping("/summary")
  InventorySummary getInventorySummary() {
    return this.inventoryService.getInventorySummary();
  }
}
