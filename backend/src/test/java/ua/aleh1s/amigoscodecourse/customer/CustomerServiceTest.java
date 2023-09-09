package ua.aleh1s.amigoscodecourse.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.amigoscodecourse.custom.UUIDGenerator;
import ua.aleh1s.amigoscodecourse.exception.DuplicateResourceException;
import ua.aleh1s.amigoscodecourse.exception.ResourceNotFoundException;
import ua.aleh1s.amigoscodecourse.storage.Buckets;
import ua.aleh1s.amigoscodecourse.storage.S3Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private static final Faker FAKER = new Faker();
    @InjectMocks
    private CustomerService underTest;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private S3Service s3Service;
    @Mock
    private Buckets buckets;
    @Mock
    private UUIDGenerator uuidGenerator;

    @Test
    void saveCustomer() {
        // given
        CustomerRegistrationRequest request = newCustomerRegistrationRequest();
        String hashedPassword = UUID.randomUUID().toString();
        when(customerRepository.existsCustomerByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn(hashedPassword);
        // when
        underTest.registerCustomer(request);
        // then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(argumentCaptor.capture());
        Customer actual = argumentCaptor.getValue();
        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isEqualTo(request.name());
        assertThat(actual.getEmail()).isEqualTo(request.email());
        assertThat(actual.getAge()).isEqualTo(request.age());
        assertThat(actual.getGender()).isEqualTo(Gender.valueOf(request.gender()));
        assertThat(actual.getPassword()).isEqualTo(hashedPassword);
    }

    @Test
    void saveCustomerThrowException() {
        // given
        CustomerRegistrationRequest request = newCustomerRegistrationRequest();
        when(customerRepository.existsCustomerByEmail(request.email())).thenReturn(true);
        // when
        // then
        assertThatThrownBy(() -> underTest.registerCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email %s is already taken".formatted(request.email()));
    }

    private CustomerRegistrationRequest newCustomerRegistrationRequest() {
        return new CustomerRegistrationRequest(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.internet().password(),
                FAKER.number().numberBetween(18, 100),
                FAKER.number().numberBetween(0, 2) % 2 == 0 ? "MALE" : "FEMALE"
        );
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
        Customer expected = newCustomer();
        when(customerRepository.findById(expected.getId()))
                .thenReturn(Optional.of(expected));
        // when
        Customer actual = underTest.getCustomerById(expected.getId());
        // then
        assertThat(actual).isEqualTo(expected);
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

    @Test
    void getCustomerByEmail() {
        // given
        Customer expected = newCustomer();
        when(customerRepository.findCustomerByEmail(expected.getEmail()))
                .thenReturn(Optional.of(expected));
        // when
        Customer actual = underTest.getCustomerByEmail(expected.getEmail());
        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void canUploadCustomerProfileImage() {
        // given
        Customer customer = newCustomer();
        String bucketName = "customer-bucket";
        String profileImageId = UUID.randomUUID().toString();
        byte[] bytes = "some_bytes".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("profile_image", bytes);
        when(customerRepository.existsById(customer.getId())).thenReturn(true);
        when(uuidGenerator.generateUUID()).thenReturn(profileImageId);
        when(buckets.getCustomer()).thenReturn(bucketName);
        // when
        underTest.uploadCustomerProfileImage(
                customer.getId(),
                multipartFile
        );
        // then
        verify(s3Service).putObject(
                bucketName,
                "profile-image/%d/%s".formatted(customer.getId(), profileImageId),
                bytes
        );
        verify(customerRepository).updateCustomerProfileImageIdByCustomerId(
                customer.getId(),
                profileImageId
        );
    }

    @Test
    void uploadCustomerProfileImageWhenCustomerDoesNotExist() {
        // given
        Integer customerId = 1;
        when(customerRepository.existsById(customerId))
                .thenReturn(false);
        // when
        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(customerId, any()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id " + customerId + " does not exist");
        // then
        verify(customerRepository).existsById(customerId);
        verifyNoMoreInteractions(customerRepository);
        verifyNoInteractions(uuidGenerator);
        verifyNoInteractions(s3Service);
        verifyNoInteractions(buckets);
    }

    @Test
    void uploadCustomerProfileImageWhenCannotPutImage() throws IOException {
        // given
        Integer customerId = 1;
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(uuidGenerator.generateUUID()).thenReturn("profile-image-id");
        when(buckets.getCustomer()).thenReturn("customer-bucket");
        when(multipartFile.getBytes()).thenThrow(IOException.class);
        // when
        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(customerId, multipartFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to upload image")
                .hasRootCauseInstanceOf(IOException.class);
        // then
        verify(customerRepository).existsById(customerId);
        verifyNoMoreInteractions(customerRepository);
        verifyNoInteractions(s3Service);
    }

    @Test
    void canDownloadCustomerProfileImage() {
        // given
        Customer customer = newCustomer();
        String profileImageId = UUID.randomUUID().toString();
        customer.setProfileImageId(profileImageId);
        String bucketName = "customer-bucket";
        byte[] expectedBytes = "some_bytes".getBytes();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(buckets.getCustomer()).thenReturn(bucketName);
        when(s3Service.getObject(
                bucketName,
                "profile-image/%d/%s".formatted(customer.getId(), profileImageId)
        )).thenReturn(expectedBytes);
        // when
        byte[] actualBytes = underTest.downloadCustomerProfileImage(customer.getId());
        // then
        assertThat(actualBytes).isEqualTo(expectedBytes);
    }

    @Test
    void downloadCustomerProfileImageWhenCustomerDoesNotExist() {
        // given
        Integer customerId = 1;
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());
        // when
        assertThatThrownBy(() -> underTest.downloadCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id " + customerId + " does not exist");
        // then
        verify(customerRepository).findById(customerId);
        verifyNoMoreInteractions(customerRepository);
        verifyNoInteractions(s3Service);
        verifyNoInteractions(buckets);
    }

    @Test
    void downloadCustomerProfileImageWhenProfileImageDoesNotExist() {
        // given
        Customer customer = newCustomer();
        when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));
        // when
        assertThatThrownBy(() -> underTest.downloadCustomerProfileImage(customer.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id " + customer.getId() + " does not have profile image");
        // then
        verify(customerRepository).findById(customer.getId());
        verifyNoMoreInteractions(customerRepository);
        verifyNoInteractions(s3Service);
        verifyNoInteractions(buckets);
    }

    private Customer newCustomer() {
        return new Customer(
                FAKER.number().numberBetween(1, 1_000_000),
                FAKER.name().fullName(),
                FAKER.internet().emailAddress(),
                FAKER.number().numberBetween(18, 100),
                FAKER.number().numberBetween(0, 2) % 2 == 0 ? Gender.MALE : Gender.FEMALE,
                FAKER.internet().password()
        );
    }
}