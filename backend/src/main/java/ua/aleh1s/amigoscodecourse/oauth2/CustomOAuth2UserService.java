package ua.aleh1s.amigoscodecourse.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ua.aleh1s.amigoscodecourse.customer.Customer;
import ua.aleh1s.amigoscodecourse.customer.CustomerRepository;
import ua.aleh1s.amigoscodecourse.exception.OAuth2AuthenticationProcessingException;
import ua.aleh1s.amigoscodecourse.oauth2.user.OAuth2UserInfo;
import ua.aleh1s.amigoscodecourse.oauth2.user.OAuth2UserInfoFactory;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;
    private final CustomerRepository customerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User auth2User = super.loadUser(userRequest);

        String providerName = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoFactory.getOAuth2UserInfo(providerName, auth2User.getAttributes());

        String email = oAuth2UserInfo.getEmail();
        if (Objects.isNull(email) || email.isBlank()) {
            throw new OAuth2AuthenticationProcessingException("Email not found from "
                    + providerName + " provider. Maybe it has private access or you didn't grant access to it.");
        }

        Optional<Customer> customerOptional = customerRepository.findCustomerByEmail(email);
        Customer customer;
        if (customerOptional.isPresent()) {
            customer = customerOptional.get();
            boolean hasAnotherAuthProvider = !customer.getAuthProvider().equals(getAuthProvider(userRequest));
            if (hasAnotherAuthProvider) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        customer.getAuthProvider() + " account. Please use your " + customer.getAuthProvider() +
                        " account to login.");
            }
            customer = updateExistingCustomer(customerOptional.get(), oAuth2UserInfo);
        } else {
            customer = registerNewCustomer(userRequest, oAuth2UserInfo);
        }
        return customer;
    }

    private Customer registerNewCustomer(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        Customer customer = new Customer(
                oAuth2UserInfo.getName(),
                oAuth2UserInfo.getEmail(),
                oAuth2UserInfo.getImageUrl(),
                getAuthProvider(oAuth2UserRequest)
        );
        return customerRepository.save(customer);
    }

    private Customer updateExistingCustomer(Customer existingCustomer, OAuth2UserInfo oAuth2UserInfo) {
        existingCustomer.setName(oAuth2UserInfo.getName());
        existingCustomer.setProfileImageUrl(oAuth2UserInfo.getImageUrl());
        return customerRepository.save(existingCustomer);
    }

    private AuthProvider getAuthProvider(OAuth2UserRequest userRequest) {
        return AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
    }
}
