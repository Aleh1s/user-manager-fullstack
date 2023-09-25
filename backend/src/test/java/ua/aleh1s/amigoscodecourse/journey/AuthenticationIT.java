package ua.aleh1s.amigoscodecourse.journey;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import ua.aleh1s.amigoscodecourse.auth.LoginRequest;
import ua.aleh1s.amigoscodecourse.auth.LoginResponse;
import ua.aleh1s.amigoscodecourse.customer.CustomerRegistrationRequest;
import ua.aleh1s.amigoscodecourse.jwt.JwtUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthenticationIT {

    private static final String AUTH_PATH = "/api/v1/auth";
    private static final String CUSTOMER_PATH = "/api/v1/customers";
    private static final Faker FAKER = new Faker();

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private JwtUtil jwtUtil;


    @Test
    public void canLogin() {
        LoginRequest loginRequest = new LoginRequest(
                FAKER.internet().emailAddress(),
                FAKER.internet().password()
        );

        webTestClient.post()
                .uri(AUTH_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized();

        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.internet().password(),
                FAKER.number().numberBetween(18, 120),
                FAKER.number().numberBetween(0, 2) % 2 == 0 ? "MALE" : "FEMALE"
        );

        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationRequest)
                .exchange()
                .expectStatus().isCreated();

        loginRequest = new LoginRequest(
                registrationRequest.email(),
                registrationRequest.password()
        );

        EntityExchangeResult<LoginResponse> result = webTestClient.post()
                .uri(AUTH_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult();

        String jwt = result.getResponseHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        String username = result.getResponseBody()
                        .username();

        assertThat(jwtUtil.isTokenNotExpired(jwt)).isTrue();
        assertThat(jwtUtil.hasTokenSameSubject(jwt, username)).isTrue();

        assertThat(username).isEqualTo(registrationRequest.email());
    }
}
