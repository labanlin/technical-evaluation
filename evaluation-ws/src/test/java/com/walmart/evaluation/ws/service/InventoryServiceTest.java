package com.walmart.evaluation.ws.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.walmart.evaluation.dto.InventoryItem;
import com.walmart.evaluation.dto.Book;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

  @Mock
  private RestTemplate restTemplate;
  @InjectMocks
  @Spy
  private InventoryService inventoryService;
  private List<Book> books;


  @Test
  public void getInventory() {
    List<InventoryItem> list = new ArrayList<>();
    list.add(new InventoryItem());
    ResponseEntity<List<InventoryItem>> responseEntity = ResponseEntity.ok(list);

    doReturn(responseEntity).when(restTemplate)
        .exchange(any(String.class), any(HttpMethod.class),
            any(HttpEntity.class), any(ParameterizedTypeReference.class));

    Assertions.assertEquals(1, inventoryService.getInventory().size());
  }

  @Test
  public void getBooks() {
    List<Book> list = new ArrayList<>();
    Book book = new Book(1, "title", "author", "desc", BigDecimal.ZERO);
    list.add(book);
    ResponseEntity<List<Book>> responseEntity = ResponseEntity.ok(list);

    doReturn(responseEntity).when(restTemplate)
        .exchange(any(String.class), any(HttpMethod.class),
            any(HttpEntity.class), any(ParameterizedTypeReference.class));

    Assertions.assertEquals(1, inventoryService.getBooks().get(0).getUpc());
  }

  @Test
  public void getInventorySummary() {
    List<InventoryItem> inventoryItems = new ArrayList<>();
    InventoryItem inventoryItem = new InventoryItem(1, 2, 4l);
    inventoryItems.add(inventoryItem);
    doReturn(inventoryItems).when(inventoryService).getInventory();

    List<Book> books = new ArrayList<>();
    Book book = new Book(1, "title", "author", "desc", BigDecimal.valueOf(12.5));
    books.add(book);
    doReturn(books).when(inventoryService).getBooks();

    Assertions.assertEquals(BigDecimal.valueOf(25.0), inventoryService.getInventorySummary().getTotalValue());
  }

  @Test
  public void getInventorySummary_withNullValues() {
    List<InventoryItem> inventoryItems = new ArrayList<>();
    InventoryItem inventoryItem = new InventoryItem(1, null, 4l);
    inventoryItems.add(inventoryItem);
    doReturn(inventoryItems).when(inventoryService).getInventory();

    List<Book> books = new ArrayList<>();
    Book book = new Book(1, "title", "author", "desc", null);
    books.add(book);
    doReturn(books).when(inventoryService).getBooks();

    Assertions.assertEquals(BigDecimal.ZERO, inventoryService.getInventorySummary().getTotalValue());
  }
}
