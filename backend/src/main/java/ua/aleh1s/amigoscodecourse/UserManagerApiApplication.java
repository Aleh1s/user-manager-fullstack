package ua.aleh1s.amigoscodecourse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.amigoscodecourse.customer.CustomerRegistrationRequest;
import ua.aleh1s.amigoscodecourse.customer.CustomerRepository;
import ua.aleh1s.amigoscodecourse.customer.CustomerService;
import ua.aleh1s.amigoscodecourse.customer.Gender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@SpringBootApplication()
public class UserManagerApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserManagerApiApplication.class, args);
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {

        };
    }
}
