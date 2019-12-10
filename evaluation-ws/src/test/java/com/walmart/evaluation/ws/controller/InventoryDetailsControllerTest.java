package com.walmart.evaluation.ws.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.walmart.evaluation.dto.Book;
import com.walmart.evaluation.dto.InventoryItem;
import com.walmart.evaluation.dto.InventorySummary;
import com.walmart.evaluation.dto.UpcSummary;
import com.walmart.evaluation.ws.service.InventoryService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

@WebMvcTest(InventoryDetailsController.class)
public class InventoryDetailsControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private InventoryService inventoryService;

  @MockBean
  private RestTemplate restTemplate;

  @BeforeEach
  public void init() {
    List<InventoryItem> inventoryItems = new ArrayList<>();
    InventoryItem inventoryItem = new InventoryItem(1, 2, 4l);
    inventoryItems.add(inventoryItem);

    doReturn(inventoryItems).when(inventoryService).getInventory();

    List<Book> books = new ArrayList<>();
    Book book = new Book(1, "title", "author", "desc", BigDecimal.valueOf(12.5));
    books.add(book);
    doReturn(books).when(inventoryService).getBooks();


    List<UpcSummary> upcSummaryList = new ArrayList<>();
    UpcSummary upcSummary = new UpcSummary(1, 2, new BigDecimal(1.2));
    upcSummaryList.add(upcSummary);
    InventorySummary summary = new InventorySummary(upcSummaryList, new BigDecimal(2.3));
    doReturn(summary).when(inventoryService).getInventorySummary();
  }

  @Test
  public void getInventorySummary() throws Exception {
    mvc.perform(MockMvcRequestBuilders
        .get("/inventory/summary")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.upcSummaryList", hasSize(1)))
        .andExpect(jsonPath("$.upcSummaryList[0].quantity", is(2)));
  }

  @Test
  public void getInventory() throws Exception {
    mvc.perform(MockMvcRequestBuilders
        .get("/inventory")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].upc", is(1)));
  }

}
