package com.walmart.evaluation.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//@SpringBootApplication
//@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class})
public class TestApplication {

  //@Value("${ws.url.inventory}")
  private String inventoryWsUrl;

  //@Value("${ws.url.books}")
  private String bookWsUrl;

  //@Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }


  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class);
  }
}
