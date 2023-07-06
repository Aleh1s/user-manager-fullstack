package ua.aleh1s.amigoscodecourse.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        int age
) {
}
