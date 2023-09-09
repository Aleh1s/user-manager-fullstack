package ua.aleh1s.amigoscodecourse.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.amigoscodecourse.custom.UUIDGenerator;
import ua.aleh1s.amigoscodecourse.exception.DuplicateResourceException;
import ua.aleh1s.amigoscodecourse.exception.ResourceNotFoundException;
import ua.aleh1s.amigoscodecourse.storage.Buckets;
import ua.aleh1s.amigoscodecourse.storage.S3Service;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UUIDGenerator uuidGenerator;
    private final S3Service s3Service;
    private final Buckets buckets;

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + id + " does not exist"));
    }

    public void registerCustomer(CustomerRegistrationRequest request) {
        String email = request.email();
        requireEmailNotPresent(email);
        Customer customer = new Customer(
                request.name(),
                request.email(),
                request.age(),
                Gender.valueOf(request.gender()),
                passwordEncoder.encode(request.password())
        );
        customerRepository.save(customer);
    }

    public void deleteCustomerById(Integer id) {
        customerRepository.deleteById(id);
    }

    public boolean existsCustomerByEmail(String email) {
        return customerRepository.existsCustomerByEmail(email);
    }

    public Page<Customer> getAllCustomers(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size < 1 || size > 100) {
            size = 25;
        }
        return customerRepository.findAll(PageRequest.of(page, size, Sort.by("id")));
    }

    @Transactional
    public void updateCustomerById(Integer id, CustomerUpdateRequest request) {
        Customer customerToUpdate = getCustomerById(id);
        if (!request.email().equals(customerToUpdate.getEmail())) {
            requireEmailNotPresent(request.email());
            customerToUpdate.setEmail(request.email());
        }
        customerToUpdate.setName(request.name());
        customerToUpdate.setAge(request.age());
        customerToUpdate.setGender(Gender.valueOf(request.gender()));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with email " + email + " does not exist"));
    }

    private void requireEmailNotPresent(String email) {
        if (existsCustomerByEmail(email)) {
            throw new DuplicateResourceException("Email %s is already taken".formatted(email));
        }
    }

    @Transactional
    public void uploadCustomerProfileImage(Integer id, MultipartFile file) {
        checkIfCustomerExistsByIdOrThrow(id);
        String profileImageId = uuidGenerator.generateUUID();
        try {
            s3Service.putObject(
                    buckets.getCustomer(),
                    "profile-image/%d/%s".formatted(id, profileImageId),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
        customerRepository.updateCustomerProfileImageIdByCustomerId(id, profileImageId);
    }

    public byte[] downloadCustomerProfileImage(Integer id) {
        Customer customer = getCustomerById(id);

        String profileImageId = customer.getProfileImageId();
        if (Objects.isNull(profileImageId)) {
            throw new ResourceNotFoundException("Customer with id " + id + " does not have profile image");
        }

        return s3Service.getObject(
                buckets.getCustomer(),
                "profile-image/%d/%s".formatted(id, profileImageId)
        );
    }

    private void checkIfCustomerExistsByIdOrThrow(Integer id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer with id " + id + " does not exist");
        }
    }
}
