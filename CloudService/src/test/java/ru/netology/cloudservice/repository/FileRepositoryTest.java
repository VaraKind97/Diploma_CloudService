package ru.netology.cloudservice.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.UserEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Disabled //Requires Docker environment
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class FileRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Test
    void saveFile() {
        UserEntity user = userRepository.save(
                UserEntity.builder()
                        .username("user")
                        .password("pass")
                        .build()
        );

        FileEntity file = FileEntity.builder()
                .filename("test.txt")
                .user(user)
                .build();

        fileRepository.save(file);

        Optional<FileEntity> result =
                fileRepository.findByFilenameAndUser(
                        "test.txt",
                        user
                );

        assertTrue(result.isPresent());
    }
}
