package com.walmart.evaluation.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class EvaluationApplication {

  @Value("${ws.url.inventory}")
  private String inventoryWsUrl;

  @Value("${ws.url.books}")
  private String bookWsUrl;

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public String inventoryWsUrl() {
    return this.inventoryWsUrl;
  }

  @Bean
  public String bookWsUrl() {
    return this.bookWsUrl;
  }

  /**
   * Build Swagger Configuration.
   * @return Docket
   */
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
  }

  public static void main(String[] args) {
    SpringApplication.run(EvaluationApplication.class);
  }
}
