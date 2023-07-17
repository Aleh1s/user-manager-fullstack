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

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIT {

    private static final Faker FAKER = new Faker();
    private static final String CUSTOMER_URI = "/api/v1/customers";

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
        CustomerCreateRequest request = newCustomerCreateRequest();

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED);

        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(
                request.name(), request.email(), request.age(), request.gender()
        );

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(expectedCustomer.getEmail()))
                .mapToInt(Customer::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Expected customer does not exist!"));

        expectedCustomer.setId(id);

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(Customer.class)
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomerById() {
        CustomerCreateRequest request = newCustomerCreateRequest();

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED);

        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(request.email()))
                .mapToInt(Customer::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Expected customer does not exist!"));

        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        CustomerCreateRequest customerCreateRequest = newCustomerCreateRequest();

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerCreateRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED);

        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .returnResult()
                .getResponseBody();

        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(customerCreateRequest.email()))
                .mapToInt(Customer::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Expected customer does not exist!"));

        CustomerUpdateRequest customerUpdateRequest = newCustomerUpdateRequest();

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerUpdateRequest)
                .exchange()
                .expectStatus().isNoContent();

        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(
                id, customerUpdateRequest.name(), customerUpdateRequest.email(),
                customerUpdateRequest.age(), customerUpdateRequest.gender()
        );

        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }

    private static CustomerCreateRequest newCustomerCreateRequest() {
        return new CustomerCreateRequest(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.number().numberBetween(10, 20),
                Gender.MALE
        );
    }

    private static CustomerUpdateRequest newCustomerUpdateRequest() {
        return new CustomerUpdateRequest(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.number().numberBetween(10, 20),
                Gender.MALE
        );
    }
}
