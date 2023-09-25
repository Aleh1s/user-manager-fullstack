package ua.aleh1s.amigoscodecourse.customer;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import ua.aleh1s.amigoscodecourse.oauth2.AuthProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity()
@Getter()
@Setter()
@Table(name = "customer")
@NoArgsConstructor()
@EqualsAndHashCode()
public class Customer implements UserDetails, OAuth2User {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "password")
    private String password;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "profile_image_id")
    private String profileImageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthProvider authProvider;

    public Customer(Integer id,
                    String name,
                    String email,
                    Integer age,
                    Gender gender,
                    String password,
                    AuthProvider authProvider) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.password = password;
        this.authProvider = authProvider;
    }

    public Customer(String name,
                    String email,
                    Integer age,
                    Gender gender,
                    String password,
                    AuthProvider authProvider) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.password = password;
        this.authProvider = authProvider;
    }

    public Customer(String name,
                    String email,
                    String profileImageUrl,
                    AuthProvider authProvider) {
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.authProvider = authProvider;
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
