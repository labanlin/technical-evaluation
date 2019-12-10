package com.walmart.evaluation.cla;

import com.walmart.evaluation.cla.service.DailyReportService;
import com.walmart.evaluation.cla.service.RetrieveFinancialDataService;
import com.walmart.evaluation.cla.service.UpdateInventoryService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Slf4j
public class ClaApplication implements CommandLineRunner {
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Autowired
  private UpdateInventoryService updateInventoryService;

  @Autowired
  private DailyReportService dailyReportService;

  @Autowired
  private RetrieveFinancialDataService retrieveFinancialDataService;

  public static void main(String[] args) {
    SpringApplication.run(ClaApplication.class, args);
  }

  /**
   * This is the entry of  the command line app.
   *
   * @param args the input from command line
   */
  public void run(String... args) throws IOException {
    if (args.length == 0) {
      log.info("Please enter the proper information.");
    }
    this.processUserInput(args);
  }

  private void processUserInput(String... args) throws IOException {
    String actionStr = args.length == 0 ? "" : args[0];
    switch (actionStr.trim().toUpperCase()) {
      case "U":
        this.updateInventoryService.adjustInventory(args);
        break;
      case "R":
        this.dailyReportService.getDailyReport(args);
        break;
      case "F":
        retrieveFinancialDataService.getFinancialData();
        break;
      default:
        StringBuilder sb = new StringBuilder("Please follow below instructions ");
        sb.append("and try again. \n\n")
            .append("For shipment/sales adjustment:\n")
            .append("U upc changeAmount, eg. 1 2 1 or 1 2 -1 \n\n")
            .append("For daily report:\n")
            .append("R inputDirectory outputDirectory, ")
            .append("eg. D C:\\Users\\abc\\output C:\\Users\\abc\\input \n\n")
            .append("For Financial Data: \n")
            .append("F");
        log.info(sb.toString());
    }
  }
}
