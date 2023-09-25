package ua.aleh1s.amigoscodecourse.customer;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CustomerDtoMapper implements Function<Customer, CustomerDto> {

    @Override
    public CustomerDto apply(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender(),
                customer.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList(),
                customer.getUsername(),
                customer.getProfileImageId(),
                customer.getProfileImageUrl()
        );
    }
}
