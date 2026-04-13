package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.dto.ErrorResponseDto;
import dev.yoxaron.cloudstorage.dto.UserAuthRequestDto;
import dev.yoxaron.cloudstorage.dto.UserAuthResponseDto;
import dev.yoxaron.cloudstorage.entity.User;
import dev.yoxaron.cloudstorage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Testcontainers
public class AuthControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    RestTestClient restTestClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();

        User existingUser = User.builder()
                .username("existing_user")
                .password(passwordEncoder.encode("password123"))
                .build();

        userRepository.save(existingUser);
    }

    @Test
    void signUp_withValidData_returns201AndSetCookie() {
        UserAuthRequestDto dto = new UserAuthRequestDto("testuser", "password123");

        EntityExchangeResult<UserAuthResponseDto> result = restTestClient.post()
                .uri("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserAuthResponseDto.class)
                .returnResult();

        assertNotNull(result.getResponseBody());
        assertEquals("testuser", result.getResponseBody().username());
        assertTrue(userRepository.findByUsername("testuser").isPresent());

        String cookie = result.getResponseHeaders().getFirst("Set-Cookie");

        restTestClient.get()
                .uri("/api/user/me")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.COOKIE, cookie)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void signUp_withExistingUsername_returns409() {
        UserAuthRequestDto dto = new UserAuthRequestDto("existing_user", "password123");

        restTestClient.post()
                .uri("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT.value())
                .expectBody(ErrorResponseDto.class);
    }

    @Test
    void signUp_withInvalidData_returns400() {
        UserAuthRequestDto dto = new UserAuthRequestDto("aaa", "");

        restTestClient.post()
                .uri("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST.value())
                .expectBody(ErrorResponseDto.class);
    }

    @Test
    void signIn_withValidData_returns200() {
        UserAuthRequestDto dto = new UserAuthRequestDto("existing_user", "password123");

        EntityExchangeResult<UserAuthResponseDto> response = restTestClient.post()
                .uri("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserAuthResponseDto.class)
                .returnResult();

        UserAuthResponseDto responseBody = response.getResponseBody();
        assertNotNull(responseBody);
        assertNotNull(response.getResponseHeaders().getFirst("Set-Cookie"));
        assertEquals("existing_user", responseBody.username());
    }

    @Test
    void signIn_withBadCredentials_returns401() {
        UserAuthRequestDto dto = new UserAuthRequestDto("non_existing_user", "password123");

        restTestClient.post()
                .uri("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void signIn_withInvalidData_returns400() {
        UserAuthRequestDto dto = new UserAuthRequestDto("123", "   ");

        restTestClient.post()
                .uri("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void signOut_happyCase_returns204() {
        String cookie = signInAndGetCookie();

        restTestClient.post()
                .uri("/api/auth/sign-out")
                .header(HttpHeaders.COOKIE, cookie)
                .exchange()
                .expectStatus().isNoContent();

        restTestClient.post()
                .uri("/api/auth/sign-out")
                .header(HttpHeaders.COOKIE, cookie)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void signOut_withUnauthorizedUser_returns401() {
        restTestClient.post()
                .uri("/api/auth/sign-out")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void me_withAuthorizedUser_returns200AndUsername() {
        String cookie = signInAndGetCookie();

        EntityExchangeResult<UserAuthResponseDto> meResult = restTestClient.get()
                .uri("/api/user/me")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.COOKIE, cookie)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserAuthResponseDto.class)
                .returnResult();

        assertEquals("existing_user", meResult.getResponseBody().username());
    }

    private String signInAndGetCookie() {
        UserAuthRequestDto dto = new UserAuthRequestDto("existing_user", "password123");

        ExchangeResult result = restTestClient.post()
                .uri("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isOk()
                .returnResult();

        return result.getResponseHeaders().getFirst("Set-Cookie");
    }
}
