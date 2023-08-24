package ua.aleh1s.amigoscodecourse.auth;

public record LoginResponse(
        String jwt,
        String username
) {
}
