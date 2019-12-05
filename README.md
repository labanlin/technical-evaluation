Overview:

This exercise is designed as a general evaluation of an applicant's experience with Java and building/consuming webservices.  In this project you will be writing two deliverables for a small book store.  


Deliverable #1:

- Using the services provided, write a RESTful service to calculate the total quantities and valuation of the book store's inventory.  (You'll use this service in the second deliverable)
  * Invoke the inventory RESTful service to get the list of items and quantities.
  * Invoke the book RESTful service to get the list of books and prices.
  * Results should contain quantities and values per upc, in addition to a total inventory valuation as JSON
  * JAR files for each service are provided and may be ran with `java -jar <filename> --server.port={server-port}`
  * Documentation for each service is provided by Swagger (http://localhost:{server-port}/swagger-ui.html#/)

Deliverable #2:

- Write a command line application that can process a given list of sales from consumers and shipments from suppliers.
  * Post sales and shipment quantity adjustments to the inventory service.
    - Sales should decrease the available inventory
    - Shipments will increase the available inventory
  * Track total sales per item, and total sales across all items using data provided by the book service.
  * Retrieve financial inventory data from the service you wrote. (see deliverable #1)
  * Output the starting inventory amounts/valuation, total daily sales, and final inventory amounts/valuation to a results.txt file.
    - Total inventory valuation is the sum of all valuations for each upc (and a upc's valuation is its quantity multiplied by its selling price point)
  * Sales and shipments will be provided via flat file representing a single day (eg 2019-12-01.txt will represent the shipments and sales for December 1st 2019), as an argument to the program (see block below for example)
   ```text
   # SALE|{upc}|{qty}
   # SHIPMENT|{upc}|{qty}
   SALE|1|3
   SHIPMENT|1|10
   SALE|2|4
   ...
   ```

Technical Requirements:

  * Deliverables are to be coded with Java 8
  * Showcase your best practices in Java
  * Demonstrate the ability to use JUnit 5 for unit and component tests.    
  * Demonstrate the ability to generate JaCoCo code coverage reports.
  * Adhere to Google code style for Java (see https://google.github.io/styleguide/javaguide.html)
