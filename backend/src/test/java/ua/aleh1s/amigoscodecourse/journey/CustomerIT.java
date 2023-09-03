package ua.aleh1s.amigoscodecourse.journey;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ua.aleh1s.amigoscodecourse.custom.CustomPageImpl;
import ua.aleh1s.amigoscodecourse.customer.*;

import java.util.List;
import java.util.stream.IntStream;

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
        CustomerDto registeredCustomer = webTestClient.get()
                .uri("%s/by-username/%s".formatted(CUSTOMER_URI, request.email()))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .returnResult()
                .getResponseBody();

        Integer id = registeredCustomer.id();
        CustomerDto expectedCustomer = new CustomerDto(
                id,
                request.name(),
                request.email(),
                request.age(),
                request.gender(),
                List.of("ROLE_USER"),
                request.email()
        );

        assertThat(registeredCustomer).isEqualTo(expectedCustomer);
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
        CustomerDto registeredCustomer = webTestClient.get()
                .uri("%s/by-username/%s".formatted(CUSTOMER_URI, requestToRegisterCustomer.email()))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .returnResult()
                .getResponseBody();

        Integer id = registeredCustomer.id();
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
        CustomerDto registeredCustomer = webTestClient.get()
                .uri("%s/by-username/%s".formatted(CUSTOMER_URI, requestToRegisterCustomer.email()))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .returnResult()
                .getResponseBody();

        int id = registeredCustomer.id();

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

    @Test
    void canGetPageOfCustomers() {
        CustomerRegistrationRequest requestToGetJwt = newCustomerCreateRequest();
        List<CustomerRegistrationRequest> requests = IntStream.range(0, 49)
                .mapToObj(i -> newCustomerCreateRequest())
                .toList();

        requests.forEach(request -> webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CREATED)
        );

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

        String bearerToken = JWT_TEMPLATE.formatted(jwt);
        Page<CustomerDto> firstPageOfCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, bearerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomPageImpl<CustomerDto>>() {})
                .returnResult()
                .getResponseBody();

        Page<CustomerDto> secondPageOfCustomers = webTestClient.get()
                .uri("%s?page=1&size=10".formatted(CUSTOMER_URI))
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, bearerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomPageImpl<CustomerDto>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(firstPageOfCustomers).isNotNull();
        assertThat(firstPageOfCustomers.getTotalElements()).isEqualTo(50);
        assertThat(firstPageOfCustomers.getTotalPages()).isEqualTo(2);
        assertThat(firstPageOfCustomers.getSize()).isEqualTo(25);
        assertThat(firstPageOfCustomers.getNumber()).isEqualTo(0);

        assertThat(secondPageOfCustomers).isNotNull();
        assertThat(secondPageOfCustomers.getTotalElements()).isEqualTo(50);
        assertThat(secondPageOfCustomers.getTotalPages()).isEqualTo(5);
        assertThat(secondPageOfCustomers.getSize()).isEqualTo(10);
        assertThat(secondPageOfCustomers.getNumber()).isEqualTo(1);
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
