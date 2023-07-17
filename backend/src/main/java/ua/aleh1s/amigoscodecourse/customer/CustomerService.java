package ua.aleh1s.amigoscodecourse.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.aleh1s.amigoscodecourse.exception.DuplicateResourceException;
import ua.aleh1s.amigoscodecourse.exception.ResourceNotFoundException;

import java.util.List;

@Service()
@RequiredArgsConstructor()
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + id + " does not exist"));
    }

    public void saveCustomer(CustomerCreateRequest request) {
        String email = request.email();
        requireEmailNotPresent(email);
        Customer customer = new Customer(
                request.name(),
                request.email(),
                request.age(),
                request.gender()
        );
        customerRepository.save(customer);
    }

    public void deleteCustomerById(Integer id) {
        customerRepository.deleteById(id);
    }

    public boolean existsCustomerById(Integer id) {
        return customerRepository.existsCustomerById(id);
    }

    public boolean existsCustomerByEmail(String email) {
        return customerRepository.existsCustomerByEmail(email);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional
    public void updateCustomerById(Integer id, CustomerUpdateRequest request) {
        Customer customerToUpdate = getCustomerById(id);
        customerToUpdate.setName(request.name());
        requireEmailNotPresent(request.email());
        customerToUpdate.setEmail(request.email());
        customerToUpdate.setAge(request.age());
    }

    private void requireEmailNotPresent(String email) {
        if (existsCustomerByEmail(email)) {
            throw new DuplicateResourceException("Email %s is already taken".formatted(email));
        }
    }
}
