# Metrodata Academy SIBKM DevSecOps Task 7: CRUD Payment Service

## Objective

1. Create a new branch in the "Payment-Service" repository.
2. Develop a new CRUD Spring Boot project named "payment-service" within the repository, incorporating the specified dependencies:
   - Spring Web
   - MySQL Driver
   - Spring Data JPA
   - Spring Boot DevTools
   - Cloud Bootstrap
   - Lombok
   - Eureka Discovery Client
   - Config Client

3. Design a table entity for the Payment service labeled "tb_m_payment," defining the following fields:
    ```java
    long id
    String mode
    Instant date
    String status
    long amount
    long orderId
    ```

4. Implement the `PaymentRequest` class for the request DTO, utilizing an Enum for the `mode` field with the specified values:
   - CASH
   - PAYPAL
   - DEBIT_CARD
   - CREDIT_CARD
   - APPLE_PAY

5. Configure the service application to utilize the settings from the config server, securely stored in GitHub.

6. Register the service application on the service registry server.

## Solution

### 1. Setting up the Project

Before proceeding, ensure you have cloned and launched the necessary projects provided earlier (i.e., config server and service registry) to ensure seamless integration with the upcoming project. Confirm that all Git credentials are readily available.

1. Clone the config server repository:

   ```bash
   git clone https://github.com/SIBKM-DevSecOps/config-server.git
   ```

2. Run the config server:

   ```bash
   cd config-server
   bash mvnw spring-boot:run
   ```

3. Open a new terminal window and clone the service registry repository:

   ```bash
   git clone https://github.com/SIBKM-DevSecOps/service-registry.git
   ```

4. Run the service registry:

   ```bash
   cd service-registry
   bash mvnw spring-boot:run
   ```

5. Visit the [Spring Initializr](https://start.spring.io/) webpage to generate the project. Ensure to select the correct dependencies and then click "Generate" to download the project.

   ![Screenshot 2023-09-15 at 09.16.37.png](_resources/Screenshot%202023-09-15%20at%2009.16.37.png)

6. Place the downloaded file in your current directory, unzip it, and navigate to the directory:

   ```bash
   unzip payment-service.zip
   cd payment-service
   ```

   Your project structure should now resemble this:
   
   ![Screenshot 2023-09-15 at 10.51.53.png](_resources/Screenshot%202023-09-15%20at%2010.51.53.png)

### 2. Configuring Application Settings

1. Rename the `application.properties` file to use YAML as the extension and complete the configuration:

   ```bash
   mv src/main/resources/application.properties src/main/resources/application.yaml
   nano src/main/resources/application.yaml
   ```

   Copy and paste the following content:

   ```yaml
   server:
     port: 8087

   spring:
     datasource:
       url: jdbc:mysql://${DB_HOST:localhost}:3306/payment_service
       username: user1
       password: 123456
       driver-class-name: com.mysql.cj.jdbc.NonRegisteringDriver
     jpa:
       database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
       show-sql: true
       hibernate:
         ddl-auto: update
     application:
       name: PAYMENT-SERVICE
     config:
       import: configserver:http://localhost:9296
   ```


### 3. Preparing the Custom Exception Handler

1. Navigate to the main working directory:

   ```bash
   cd src/main/java/com/example/paymentservice
   ```

2. Create a class to represent the custom exception object:

   ```bash
   mkdir exception
   nano exception/CustomException.java
   ```

   Copy and paste the following content:

   ```java
   package com.example.paymentservice.exception;

   import lombok.Data;

   @Data
   public class CustomException extends RuntimeException {
       private String error;
       private int status;

       public CustomException(String message, String error, int status) {
           super(message);
           this.error = error;
           this.status = status;
       }
   }
   ```

3. Create a response DTO class for the custom error message:

   ```bash
   mkdir model
   nano model/ErrorMessage.java
   ```

   Copy and paste the following content:

   ```java
   package com.example.paymentservice.model;

   import lombok.AllArgsConstructor;
   import lombok.Builder;
   import lombok.Data;
   import lombok.NoArgsConstructor;

   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   @Builder
   public class ErrorMessage {
       private String message;
       private String error;
   }
   ```

4. Create a utility class for custom error handling:

   ```bash
   nano exception/RestResponseExceptionHandler.java
   ```

   Copy and paste the following content:

   ```java
   package com.example.paymentservice.exception;

   import com.example.paymentservice.model.ErrorMessage;
   import org.springframework.http.HttpStatus;
   import org.springframework.http.ResponseEntity;
   import org.springframework.web.bind.annotation.ControllerAdvice;
   import org.springframework.web.bind.annotation.ExceptionHandler;
   import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

   @ControllerAdvice
   public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
       @ExceptionHandler(CustomException.class)
       public ResponseEntity<ErrorMessage> customException(CustomException customException) {
           return new ResponseEntity<>(ErrorMessage.builder()
                   .message(customException.getMessage())
                   .error(customException.getError())
                   .build(),
                   HttpStatus.valueOf(customException.getStatus()));
       }
   }
   ```

### 4. Implementing CRUD Operations

1. Create the table entity named "tb_m_payment" with the required fields:

   ```bash
   mkdir entity
   nano entity/Payment.java
   ```

   Copy and paste the following content:

   ```java
   package com.example.paymentservice.entity;

   import lombok.AllArgsConstructor;
   import lombok.Builder;
   import lombok.Data;
   import lombok.NoArgsConstructor;

   import javax.persistence.*;
   import java.time.Instant;

   @Builder
   @AllArgsConstructor
   @NoArgsConstructor
   @Entity
   @Data
   @Table(name = "tb_m_payment")
   public class Payment {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private long id;
       private String mode;
       private Instant date;
       private String status;
       private long amount;
       private long orderId;
   }
   ```

2. Create a request DTO class for payment data:

   ```bash
   nano model/PaymentRequest.java
   ```

   Copy and paste the following content:

   ```java
   package com.example.paymentservice.model;

   import lombok.AllArgsConstructor;
   import lombok.Data;
   import lombok.NoArgsConstructor;

   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class PaymentRequest {
       private PaymentMode mode;
       private long amount;
       private long orderId;

       public enum PaymentMode {
           CASH,
           PAYPAL,
           DEBIT_CARD,
           CREDIT_CARD,
           APPLE_PAY
       }

    }
   ```

3. Create a response DTO class for payment data:

   ```bash
   nano model/PaymentResponse.java
   ```

   Copy and paste the following content:

   ```java
   package com.example.paymentservice.model;

   import lombok.AllArgsConstructor;
   import lombok.Data;
   import lombok.NoArgsConstructor;

   import java.time.Instant;

   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class PaymentResponse {
       private long id;
       private String mode;
       private Instant date;
       private String status;
       private long amount;
       private long orderId;
   }
   ```

4. Create the repository for payment data:

   ```bash
   nano repository/PaymentRepository.java
   ```

   Copy and paste the following content:

   ```java
   package com.example.paymentservice.repository;

   import com.example.paymentservice.entity.Payment;
   import org.springframework.data.jpa.repository.JpaRepository;
   import org.springframework.stereotype.Repository;

   @Repository
   public interface PaymentRepository extends JpaRepository<Payment, Long> {
   }
   ```

5. Create the service interface for payment data:

   ```bash
   mkdir service
   nano service/PaymentService.java
   ```

   Copy and paste the following content:

   ```java
    package com.example.paymentservice.service;

    import com.example.paymentservice.model.PaymentRequest;
    import com.example.paymentservice.model.PaymentResponse;

    import java.util.List;

    public interface PaymentService {
        List<PaymentResponse> getAll();
        PaymentResponse getById(long id);
        PaymentResponse create(PaymentRequest paymentRequest);

    }
   ```

6. Create the service implementation for payment data:

   ```bash
   nano service/PaymentServiceImpl.java
   ```

   Copy and paste the following content:

   ```java
    package com.example.paymentservice.service;

    import com.example.paymentservice.entity.Payment;
    import com.example.paymentservice.exception.CustomException;
    import com.example.paymentservice.model.PaymentRequest;
    import com.example.paymentservice.model.PaymentResponse;
    import com.example.paymentservice.repository.PaymentRepository;
    import lombok.AllArgsConstructor;
    import org.springframework.beans.BeanUtils;
    import org.springframework.core.ParameterizedTypeReference;
    import org.springframework.http.HttpMethod;
    import org.springframework.stereotype.Service;

    import java.time.Instant;
    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    @AllArgsConstructor
    public class PaymentServiceImpl implements PaymentService {
        private PaymentRepository paymentRepository;

        @Override
        public List<PaymentResponse> getAll() {
            return paymentRepository.findAll()
                    .stream()
                    .map(this::mappingPaymentToPaymentResponses).collect(Collectors.toList());
        }

        @Override
        public PaymentResponse getById(long id) {
            Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new CustomException(
                        "Payment with the given id " + id + " was not found.",
                        "PAYMENT_NOT_FOUND",
                        404
                    ));
            return mappingPaymentToPaymentResponses(payment);
        }

        @Override
        public PaymentResponse create(PaymentRequest paymentRequest) {
            Payment payment = Payment.builder()
                    .mode(paymentRequest.getMode().name())
                    .amount(paymentRequest.getAmount())
                    .orderId(paymentRequest.getOrderId())
                    .status("SUCCESS")
                    .date(Instant.now())
                    .build();

            Payment res = paymentRepository.save(payment);
            return mappingPaymentToPaymentResponses(res);
        }

        private PaymentResponse mappingPaymentToPaymentResponses(Payment payment) {
            PaymentResponse paymentResponse = new PaymentResponse();
            BeanUtils.copyProperties(payment, paymentResponse);
            return paymentResponse;
        }
    }

   ```

7. Create the controller for the application:

   ```bash
   mkdir controller
   nano controller/PaymentController.java
   ```

   Copy and paste the following content:

   ```java
    package com.example.paymentservice.controller;

    import com.example.paymentservice.entity.Payment;
    import com.example.paymentservice.model.PaymentRequest;
    import com.example.paymentservice.model.PaymentResponse;
    import com.example.paymentservice.service.PaymentService;
    import lombok.AllArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import javax.ws.rs.Path;
    import java.util.List;

    @RestController
    @RequestMapping("/payment")
    @AllArgsConstructor
    public class PaymentController {
        private PaymentService paymentService;

        @GetMapping
        public ResponseEntity<List<PaymentResponse>> getAll() {
            return new ResponseEntity<>(paymentService.getAll(), HttpStatus.OK);
        }

        @GetMapping("/{id}")
        public ResponseEntity<PaymentResponse> getById(@PathVariable long id) {
            return new ResponseEntity<>(paymentService.getById(id), HttpStatus.OK);
        }

        @PostMapping
        public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest paymentRequest) {
            return new ResponseEntity<>(paymentService.create(paymentRequest), HttpStatus.OK);
        }
    }

   ```

8. Run the application. 
    ```bash 
    cd <Project Root Path>
    bash mvnw spring-boot:run
    ```

    Your project structure should look similar to this.

    ![Screenshot 2023-09-15 at 11.40.19.png](_resources/Screenshot%202023-09-15%20at%2019.32.48.png)

## 4. Testing

1. Test the create endpoint of the payment service using Postman.

   ![Screenshot 2023-09-15 at 11.40.19.png](_resources/Screenshot%202023-09-15%20at%2011.40.19.png)
   
2. Test the list endpoint of the payment service.

   ![Screenshot 2023-09-15 at 11.40.54.png](_resources/Screenshot%202023-09-15%20at%2011.40.54.png)
   
3. Test the detail endpoint of the payment service.

   ![Screenshot 2023-09-15 at 11.41.23.png](_resources/Screenshot%202023-09-15%20at%2011.41.23.png)