package ua.aleh1s.amigoscodecourse.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.aleh1s.amigoscodecourse.jwt.JwtUtil;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JwtUtil jwtUtil;
    private final CustomerDtoMapper customerDtoMapper;

    @PostMapping()
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.saveCustomer(request);
        String jwt = jwtUtil.issueToken(request.email(), "ROLE_USER");
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .build();
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<CustomerDto> getAllCustomers(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "25") int size) {
        return customerService.getAllCustomers(page, size)
                .map(customerDtoMapper);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDto getCustomerById(@PathVariable("id") Integer id) {
        Customer customerById = customerService.getCustomerById(id);
        return customerDtoMapper.apply(customerById);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomerById(@PathVariable("id") Integer id) {
        customerService.deleteCustomerById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCustomerById(@PathVariable("id") Integer id, @RequestBody CustomerUpdateRequest request) {
        customerService.updateCustomerById(id, request);
    }

    @GetMapping("/by-username/{username}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDto getCustomerByUsername(@PathVariable("username") String username) {
        Customer customerByEmail = customerService.getCustomerByEmail(username);
        return customerDtoMapper.apply(customerByEmail);
    }
}
