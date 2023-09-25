package ua.aleh1s.amigoscodecourse.oauth2.user;

import org.springframework.stereotype.Component;
import ua.aleh1s.amigoscodecourse.exception.OAuth2AuthenticationProcessingException;
import ua.aleh1s.amigoscodecourse.oauth2.AuthProvider;

import java.util.Map;

@Component
public class OAuth2UserInfoFactory {
    public OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.name())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.name())) {
            return new GitHubOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
