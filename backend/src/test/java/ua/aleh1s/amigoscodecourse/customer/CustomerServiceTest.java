package ua.aleh1s.amigoscodecourse.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.aleh1s.amigoscodecourse.exception.DuplicateResourceException;
import ua.aleh1s.amigoscodecourse.exception.ResourceNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private static final Faker FAKER = new Faker();
    @InjectMocks
    private CustomerService underTest;
    @Mock
    private CustomerRepository customerRepository;

    @Test
    void saveCustomer() {
        // given
        CustomerCreateRequest request = new CustomerCreateRequest(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.number().numberBetween(10, 20),
                Gender.MALE
        );
        when(customerRepository.existsCustomerByEmail(request.email())).thenReturn(false);
        // when
        underTest.saveCustomer(request);
        // then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(argumentCaptor.capture());
        Customer actual = argumentCaptor.getValue();
        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isEqualTo(request.name());
        assertThat(actual.getEmail()).isEqualTo(request.email());
        assertThat(actual.getAge()).isEqualTo(request.age());
    }

    @Test
    void saveCustomerThrowException() {
        // given
        CustomerCreateRequest request = new CustomerCreateRequest(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.number().numberBetween(10, 20),
                Gender.MALE
        );
        when(customerRepository.existsCustomerByEmail(request.email())).thenReturn(true);
        // when
        // then
        assertThatThrownBy(() -> underTest.saveCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email %s is already taken".formatted(request.email()));
    }

    @Test
    void deleteCustomerById() {
        // given
        int id = 1;
        // when
        underTest.deleteCustomerById(id);
        // then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void existsCustomerById() {
        // given
        int id = 1;
        // when
        underTest.existsCustomerById(id);
        // then
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void existsCustomerByEmail() {
        // given
        String email = FAKER.internet().emailAddress();
        // when
        underTest.existsCustomerByEmail(email);
        // then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void getCustomerById() {
        // given
        int id = FAKER.number().numberBetween(1, 10);
        Customer customer = new Customer(
                id,
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.number().numberBetween(10, 20),
                Gender.MALE
        );
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        // when
        Customer actualCustomer = underTest.getCustomerById(id);
        // then
        assertEquals(customer, actualCustomer);
    }

    @Test
    void getCustomerByIdThrowException() {
        // given
        int id = FAKER.number().numberBetween(1, 10);
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer with id %d does not exist".formatted(id));
    }
}