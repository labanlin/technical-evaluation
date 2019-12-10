package com.walmart.evaluation.cla.service;

import com.walmart.evaluation.cla.Constants;
import com.walmart.evaluation.cla.utils.ClaUtils;
import com.walmart.evaluation.dto.InventoryAdjustment;
import com.walmart.evaluation.dto.InventorySummary;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DailyReportService {

  @Value("${ws.url.evaluation}")
  private String evaluationWsUrl;

  private final UpdateInventoryService updateInventoryService;
  private final RestTemplate restTemplate;

  public DailyReportService(UpdateInventoryService updateInventoryService,
                            RestTemplate restTemplate) {
    this.updateInventoryService = updateInventoryService;
    this.restTemplate = restTemplate;
  }

  /**
   * Get daily report of the inventory.
   *
   * @param args user input in the format of: D input output.
   *             eg. D C:\Users\abc\output C:\Users\abc\input
   */
  public void getDailyReport(String... args) throws IOException {
    if (!isValidDirectory(args)) {
      log.error("Input or output directory doesn't exist.");
      return;
    }
    Path[] paths = buildInputAndOutputPaths(args);
    if (paths == null) {
      log.error("Input file doesn't exist.");
      return;
    }

    String inventorySummaryWsUrl = this.evaluationWsUrl + "/inventory/summary";
    InventorySummary oldInventory = restTemplate
        .getForObject(inventorySummaryWsUrl, InventorySummary.class);

    Files.lines(paths[0]).forEach(line -> this.processLine(line));

    InventorySummary newInventory = restTemplate
        .getForObject(inventorySummaryWsUrl, InventorySummary.class);

    List<String> resultLines = this.buildResultLines(oldInventory, newInventory);

    Files.write(paths[1], resultLines, StandardCharsets.UTF_8);
    log.info("Report is processed successfully.");
  }

  private List<String> buildResultLines(InventorySummary oldInventory,
                                        InventorySummary newInventory) {
    List<String> resultLines = new ArrayList<>();
    resultLines.add(Constants.TITLE_DAY_BEGINNING_STATICS);

    int oldInventoryAmount = oldInventory.getUpcSummaryList()
        .stream().mapToInt(x -> x.getQuantity()).sum();

    String resultLine = new StringBuilder(Constants.LABEL_AMOUNT).append(oldInventoryAmount)
        .append(Constants.SPACES_5).append(Constants.LABEL_VALUATION)
        .append(oldInventory.getTotalValue()).toString();

    resultLines.add(resultLine);

    resultLines.add(Constants.TITLE_DAY_END_STATICS);
    int newInventoryAmount = newInventory.getUpcSummaryList()
        .stream().mapToInt(x -> x.getQuantity()).sum();

    resultLine = new StringBuilder(Constants.LABEL_AMOUNT).append(newInventoryAmount)
        .append(Constants.SPACES_5).append(Constants.LABEL_VALUATION)
        .append(newInventory.getTotalValue()).toString();

    resultLines.add(resultLine);
    resultLines.add(Constants.LABEL_TOTAL_SALES + (newInventoryAmount - oldInventoryAmount));

    return resultLines;
  }

  private boolean isValidDirectory(String[] args) {
    if (args.length < 3 || !Files.isDirectory(Paths.get(args[1]))
        || !Files.isDirectory(Paths.get(args[2]))) {
      return false;
    }
    return true;
  }

  private Path[] buildInputAndOutputPaths(String... args) throws IOException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMATTER);
    String dateStr = LocalDate.now().format(formatter);

    String fileName = new StringBuilder(dateStr).append(Constants.EXTENSION_TXT).toString();
    Path inputFilePath = Paths.get(args[1], fileName);
    log.info("input file path :" + inputFilePath);
    if (Files.notExists(inputFilePath)) {
      return null;
    }
    Path outputFilePath = Paths.get(args[2], fileName);
    Files.deleteIfExists(outputFilePath);

    return new Path[] {inputFilePath, outputFilePath};
  }

  private void processLine(String line) {
    if (!isValidLine(line)) {
      log.error("Invalid line :" + line);
      return;
    }
    String[] columnsOfLine = line.split(Constants.INLINE_SEPARATOR);
    try {
      InventoryAdjustment inventoryAdjustment = new InventoryAdjustment(
          Integer.parseInt(columnsOfLine[1]), Integer.parseInt(columnsOfLine[2]));

      this.updateInventoryService.adjustInventory(inventoryAdjustment);
    } catch (Exception e) {
      log.error("Failed to process line :" + line, e);
    }


  }

  private boolean isValidLine(String line) {
    line = StringUtils.trimWhitespace(line);

    if (line == null || !(line.startsWith(Constants.SALE) || line.startsWith(Constants.SHIPMENT))) {
      return false;
    }
    String[] columnsOfLine = line.split(Constants.INLINE_SEPARATOR);
    if (columnsOfLine.length < 3
        || !ClaUtils.isNumber(columnsOfLine[1])
        || !ClaUtils.isNumber(columnsOfLine[2])) {
      return false;
    }
    return true;
  }
}
