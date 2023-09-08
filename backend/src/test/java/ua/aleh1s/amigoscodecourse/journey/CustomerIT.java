package ua.aleh1s.amigoscodecourse.journey;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import ua.aleh1s.amigoscodecourse.custom.CustomPageImpl;
import ua.aleh1s.amigoscodecourse.customer.*;
import ua.aleh1s.amigoscodecourse.jwt.JwtUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIT {

    private static final Faker FAKER = new Faker();
    private static final String CUSTOMER_URI = "/api/v1/customers";
    private static final String BEARER_TOKEN_TEMPLATE = "Bearer %s";

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private JwtUtil jwtUtil;

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

        String jwtToken = BEARER_TOKEN_TEMPLATE.formatted(jwt);
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
                request.email(),
                null
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

        String jwtToken = BEARER_TOKEN_TEMPLATE.formatted(jwt);
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

        String jwtToken = BEARER_TOKEN_TEMPLATE.formatted(jwt);
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
                customerUpdateRequest.email(),
                null
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

        String bearerToken = BEARER_TOKEN_TEMPLATE.formatted(jwt);
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

    @Test
    void canUploadAndDownloadProfileImage() throws IOException {
        CustomerRegistrationRequest registrationRequest = newCustomerCreateRequest();

        String jwt = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .returnResult(Void.class)
                .getResponseHeaders()
                .getFirst(AUTHORIZATION);

        String username = jwtUtil.extractSubject(jwt);
        String bearerToken = BEARER_TOKEN_TEMPLATE.formatted(jwt);

        CustomerDto customer = webTestClient.get()
                .uri(CUSTOMER_URI + "/by-username/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, bearerToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(customer.profileImageId()).isNull();

        Resource profileImage = new ClassPathResource(
                "images/%s.jpg".formatted(customer.gender().name().toLowerCase())
        );

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", profileImage);

        webTestClient.post()
                .uri(CUSTOMER_URI + "/{id}/profile-image", customer.id())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(AUTHORIZATION, bearerToken)
                .bodyValue(bodyBuilder.build())
                .exchange()
                .expectStatus()
                .isCreated();

        String profileImageId = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", customer.id())
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, bearerToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDto.class)
                .returnResult()
                .getResponseBody()
                .profileImageId();

        assertThat(profileImageId).isNotNull();

        byte[] actualProfileImageBytes = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}/profile-image", customer.id())
                .accept(MediaType.IMAGE_JPEG)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(byte[].class)
                .returnResult()
                .getResponseBody();
        byte[] expectedProfileImageBytes = profileImage.getContentAsByteArray();

        assertThat(actualProfileImageBytes).isEqualTo(expectedProfileImageBytes);
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
