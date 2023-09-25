package ua.aleh1s.amigoscodecourse.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.aleh1s.amigoscodecourse.BaseTestcontainers;
import ua.aleh1s.amigoscodecourse.oauth2.AuthProvider;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends BaseTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    public void setUp() {
        underTest.deleteAll();
    }

    @Test
    void existsCustomerById() {
        // given
        Customer customer = newCustomer();
        underTest.save(customer);
        Integer id = findCustomerIdByEmail(customer.getEmail());
        // when
        boolean exists = underTest.existsCustomerById(id);
        // then
        assertTrue(exists);
    }

    @Test
    void existsCustomerByEmail() {
        // given
        Customer customer = newCustomer();
        underTest.save(customer);
        // when
        boolean exists = underTest.existsCustomerByEmail(customer.getEmail());
        // then
        assertTrue(exists);
    }

    private Integer findCustomerIdByEmail(String email) {
        return underTest.findAll().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Customer not found"));
    }

    @Test
    void updateCustomerProfileImageIdByCustomerId() {
        // given
        Customer customer = newCustomer();
        String expectedProfileImageId = UUID.randomUUID().toString();

        underTest.save(customer);

        Integer id = underTest.findAll().stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst()
                .orElseThrow()
                .getId();

        // when
        underTest.updateCustomerProfileImageIdByCustomerId(id, expectedProfileImageId);

        // then
        underTest.findById(id)
                .ifPresentOrElse(c -> assertThat(c.getProfileImageId()).isEqualTo(expectedProfileImageId),
                        () -> fail("Customer not found"));
    }

    private Customer newCustomer() {
        return new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.number().numberBetween(18, 100),
                FAKER.number().numberBetween(0, 2) % 2 == 0 ? Gender.MALE : Gender.FEMALE,
                FAKER.internet().password(),
                AuthProvider.CUSTOM);
    }
}