package ua.aleh1s.amigoscodecourse.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua.aleh1s.amigoscodecourse.customer.Customer;
import ua.aleh1s.amigoscodecourse.customer.CustomerDto;
import ua.aleh1s.amigoscodecourse.customer.CustomerDtoMapper;
import ua.aleh1s.amigoscodecourse.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomerDtoMapper customerDtoMapper;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.username(),
                loginRequest.password()
        ));

        Customer customer = (Customer) authenticate.getPrincipal();
        CustomerDto customerDto = customerDtoMapper.apply(customer);

        String jwt = jwtUtil.issueToken(
                customerDto.username(),
                customerDto.roles()
        );

        return new LoginResponse(jwt, customerDto.username());
    }
}
