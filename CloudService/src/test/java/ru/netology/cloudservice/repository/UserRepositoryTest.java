package ru.netology.cloudservice.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.model.UserEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Disabled //Requires Docker environment
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");


    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser() {

        UserEntity user = UserEntity.builder()
                .username("user")
                .password("password")
                .build();

        UserEntity saved =
                userRepository.save(user);

        assertNotNull(saved.getId());

        assertEquals(
                "user",
                saved.getUsername()
        );
    }

    @Test
    void findByUsername() {

        UserEntity user = UserEntity.builder()
                .username("admin")
                .password("test")
                .build();

        userRepository.save(user);

        Optional<UserEntity> result =
                userRepository.findByUsername(
                        "admin"
                );

        assertTrue(result.isPresent());

        assertEquals(
                "admin",
                result.get().getUsername()
        );
    }

    @Test
    void findByUsernameNotFound() {

        Optional<UserEntity> result =
                userRepository.findByUsername(
                        "unknown"
                );

        assertTrue(result.isEmpty());
    }

}
