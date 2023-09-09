package ua.aleh1s.amigoscodecourse.customer;

import jakarta.validation.constraints.*;

public record CustomerRegistrationRequest(
        @NotNull(message = "Name cannot be null")
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        String name,
        @NotNull(message = "Email cannot be null")
        @Email(message = "Email should be valid")
        String email,
        @NotNull(message = "Password cannot be null")
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
        String password,
        @Min(value = 18, message = "Age should not be less than 18")
        @Max(value = 120, message = "Age should not be greater than 120")
        @NotNull(message = "Age cannot be null")
        Integer age,
        @NotNull(message = "Gender cannot be null")
        @Pattern(regexp = "^MALE|FEMALE$", message = "Gender should be MALE or FEMALE")
        String gender
) {
}