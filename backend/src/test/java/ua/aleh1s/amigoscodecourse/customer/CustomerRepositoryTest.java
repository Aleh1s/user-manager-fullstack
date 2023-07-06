package ua.aleh1s.amigoscodecourse.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.aleh1s.amigoscodecourse.BaseTestcontainers;

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
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                20
        );
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
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                20
        );
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
}