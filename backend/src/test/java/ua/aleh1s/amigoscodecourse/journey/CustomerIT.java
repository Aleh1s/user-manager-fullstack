package ua.aleh1s.amigoscodecourse.journey;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ua.aleh1s.amigoscodecourse.customer.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIT {

    private static final Faker FAKER = new Faker();
    private static final String CUSTOMER_URI = "/api/v1/customers";
    private static final String JWT_TEMPLATE = "Bearer %s";

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void canRegisterCustomer() {
        CustomerRegistrationRequest request = newCustomerCreateRequest();

        String jwt = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CREATED)
                .returnResult(Void.class)
                .getResponseHeaders()
                .getFirst(AUTHORIZATION);

        String jwtToken = JWT_TEMPLATE.formatted(jwt);
        List<CustomerDto> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(customer -> customer.email().equals(request.email()))
                .mapToInt(CustomerDto::id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Expected customer does not exist!"));

        CustomerDto expectedCustomer = new CustomerDto(
                id,
                request.name(),
                request.email(),
                request.age(),
                request.gender(),
                List.of("ROLE_USER"),
                request.email()
        );

        assertThat(allCustomers).contains(expectedCustomer);

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .exchange()
                .expectBody(CustomerDto.class)
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomerById() {
        CustomerRegistrationRequest requestToGetJwt = newCustomerCreateRequest();
        CustomerRegistrationRequest requestToRegisterCustomer = newCustomerCreateRequest();

        String jwt = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestToGetJwt)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .returnResult(Void.class)
                .getResponseHeaders()
                .getFirst(AUTHORIZATION);

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestToRegisterCustomer)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED);

        String jwtToken = JWT_TEMPLATE.formatted(jwt);
        List<CustomerDto> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(customer -> customer.email().equals(requestToRegisterCustomer.email()))
                .mapToInt(CustomerDto::id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Expected customer does not exist!"));

        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        CustomerRegistrationRequest requestToGetJwt = newCustomerCreateRequest();
        CustomerRegistrationRequest requestToRegisterCustomer = newCustomerCreateRequest();

        String jwt = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestToGetJwt)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .returnResult(Void.class)
                .getResponseHeaders()
                .getFirst(AUTHORIZATION);

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestToRegisterCustomer)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED);

        String jwtToken = JWT_TEMPLATE.formatted(jwt);
        List<CustomerDto> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(customer -> customer.email().equals(requestToRegisterCustomer.email()))
                .mapToInt(CustomerDto::id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Expected customer does not exist!"));

        CustomerUpdateRequest customerUpdateRequest = newCustomerUpdateRequest();

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .bodyValue(customerUpdateRequest)
                .exchange()
                .expectStatus().isNoContent();

        CustomerDto updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .returnResult()
                .getResponseBody();

        CustomerDto expectedCustomer = new CustomerDto(
                id,
                customerUpdateRequest.name(),
                customerUpdateRequest.email(),
                customerUpdateRequest.age(),
                customerUpdateRequest.gender(),
                List.of("ROLE_USER"),
                customerUpdateRequest.email()
        );

        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }

    private static CustomerRegistrationRequest newCustomerCreateRequest() {
        return new CustomerRegistrationRequest(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.internet().password(),
                FAKER.number().numberBetween(18, 100),
                FAKER.number().numberBetween(0, 2) % 2 == 0 ? Gender.MALE : Gender.FEMALE
        );
    }

    private static CustomerUpdateRequest newCustomerUpdateRequest() {
        return new CustomerUpdateRequest(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.number().numberBetween(18, 100),
                FAKER.number().numberBetween(0, 2) % 2 == 0 ? Gender.MALE : Gender.FEMALE
        );
    }
}
