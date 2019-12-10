package com.walmart.evaluation.ws.service;

import com.walmart.evaluation.dto.Book;
import com.walmart.evaluation.dto.InventoryItem;
import com.walmart.evaluation.dto.InventorySummary;
import com.walmart.evaluation.dto.UpcSummary;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class InventoryService {

  private final String inventoryWsUrl;
  private final String bookWsUrl;
  private final RestTemplate restTemplate;

  /**
   * Constructor of class InventoryService.
   *
   * @param restTemplate   Rest Template
   * @param inventoryWsUrl url of inventory WS
   * @param bookWsUrl      url of book WS
   */
  public InventoryService(RestTemplate restTemplate, String inventoryWsUrl, String bookWsUrl) {
    this.restTemplate = restTemplate;
    this.inventoryWsUrl = inventoryWsUrl;
    this.bookWsUrl = bookWsUrl;
  }

  /**
   * Get inventory details of the items.
   *
   * @return {@code List<InventoryItem>}
   */
  public List<InventoryItem> getInventory() {
    final List<InventoryItem> body = this.restTemplate
        .exchange(inventoryWsUrl + "/inventory", HttpMethod.GET, HttpEntity.EMPTY,
            new ParameterizedTypeReference<List<InventoryItem>>() {
            }).getBody();
    return body;
  }

  /**
   * Get all books details.
   *
   * @return {@code List<Book>}
   */
  public List<Book> getBooks() {
    return this.restTemplate
        .exchange(this.bookWsUrl + "/books", HttpMethod.GET, HttpEntity.EMPTY,
            new ParameterizedTypeReference<List<Book>>() {
            }).getBody();
  }

  /**
   * Get inventory summary.
   *
   * @return {@code InventorySummary}
   */
  public InventorySummary getInventorySummary() {
    List<InventoryItem> inventory = this.getInventory();
    List<Book> books = this.getBooks();

    Map<Integer, List<Book>> upcToBooksMap = books.parallelStream()
        .collect(Collectors.groupingBy(Book::getUpc));

    List<UpcSummary> upcSummaryList = inventory.parallelStream()
        .map(inventoryItem -> this.buildUpcSummary(inventoryItem, upcToBooksMap))
        .collect(Collectors.toList());

    BigDecimal decimalOne = BigDecimal.valueOf(1);
    BigDecimal inventoryTotalValue = upcSummaryList.parallelStream()
        .reduce(decimalOne,
            (partialResult, x) -> partialResult.multiply(
                Optional.ofNullable(x.getTotalValue()).orElse(decimalOne)),
            (x, y) -> x.multiply(y));

    return new InventorySummary(upcSummaryList, inventoryTotalValue);
  }

  private UpcSummary buildUpcSummary(InventoryItem inventoryItem, Map<Integer,
      List<Book>> upcToBooksMap) {

    Integer quantity = inventoryItem.getQuantity() == null ? 0 : inventoryItem.getQuantity();
    Book book = Optional.ofNullable(upcToBooksMap.get(inventoryItem.getUpc()))
        .orElse(new ArrayList<>()).stream().findFirst().orElse(new Book());

    BigDecimal price = book.getPrice() == null ? BigDecimal.ZERO : book.getPrice();
    BigDecimal totalValue = price.multiply(BigDecimal.valueOf(quantity));

    return new UpcSummary(book.getUpc(), quantity, totalValue);
  }
}