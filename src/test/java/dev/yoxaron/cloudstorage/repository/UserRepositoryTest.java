package dev.yoxaron.cloudstorage.repository;

import dev.yoxaron.cloudstorage.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        userRepository.save(user);

        Optional<User> savedUser = userRepository.findByUsername("username");

        assertTrue(savedUser.isPresent());
        assertEquals(savedUser.get().getId(), user.getId());
        assertEquals(savedUser.get().getUsername(), user.getUsername());
    }
}