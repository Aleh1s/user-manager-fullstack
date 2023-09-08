package ua.aleh1s.amigoscodecourse.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.amigoscodecourse.jwt.JwtUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JwtUtil jwtUtil;
    private final CustomerDtoMapper customerDtoMapper;

    @PostMapping()
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.registerCustomer(request);
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

    @PostMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadCustomerProfileImage(@PathVariable("id") Integer id,
                                           @RequestParam("file") MultipartFile file) {
        customerService.uploadCustomerProfileImage(id, file);
    }

    @GetMapping(value = "/{id}/profile-image", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] downloadCustomerProfileImage(@PathVariable("id") Integer id) {
        return customerService.downloadCustomerProfileImage(id);
    }
}
