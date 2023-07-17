package ua.aleh1s.amigoscodecourse.customer;

public record CustomerCreateRequest(
        String name,
        String email,
        int age,
        Gender gender
) {
}
