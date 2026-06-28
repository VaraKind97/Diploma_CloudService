package ru.netology.cloudservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.netology.cloudservice.dto.response.FileResponse;
import ru.netology.cloudservice.exceptions.AddingFileException;
import ru.netology.cloudservice.exceptions.DeleteFileException;
import ru.netology.cloudservice.exceptions.GetFileException;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.UserEntity;
import ru.netology.cloudservice.repository.FileRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private FileService fileService;

    @Test
    void uploadFile() {

        UserEntity user = UserEntity.builder()
                .id(1L)
                .build();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.txt",
                        "text/plain",
                        "hello".getBytes()
                );

        when(authService.getUserFromToken("token"))
                .thenReturn(user);

        fileService.uploadFile(
                "token",
                "test.txt",
                file
        );

        verify(fileRepository)
                .save(any(FileEntity.class));

    }

    @Test
    void downloadFile() throws Exception {

        Path tempFile =
                Files.createTempFile("download", ".txt");

        byte[] expected = "hello".getBytes();

        Files.write(tempFile, expected);

        UserEntity user = UserEntity.builder()
                .id(1L)
                .build();

        FileEntity entity = FileEntity.builder()
                .filename("test.txt")
                .filePath(tempFile.toString())
                .user(user)
                .build();

        when(authService.getUserFromToken("token"))
                .thenReturn(user);

        when(fileRepository.findByFilenameAndUser(
                "test.txt",
                user))
                .thenReturn(Optional.of(entity));

        byte[] result =
                fileService.downloadFile(
                        "token",
                        "test.txt"
                );

        assertArrayEquals(expected, result);
    }

    @Test
    void downloadFileNotFound() {

        UserEntity user =
                UserEntity.builder().id(1L).build();

        when(authService.getUserFromToken("token"))
                .thenReturn(user);

        when(fileRepository.findByFilenameAndUser(
                "missing.txt",
                user))
                .thenReturn(Optional.empty());

        GetFileException ex =
                assertThrows(
                        GetFileException.class,
                        () -> fileService.downloadFile(
                                "token",
                                "missing.txt")
                );

        assertEquals(
                "File not found",
                ex.getMessage()
        );
    }


    @Test
    void deleteFile() throws Exception {

        Path tempFile =
                Files.createTempFile("delete", ".txt");

        UserEntity user =
                UserEntity.builder().id(1L).build();

        FileEntity file =
                FileEntity.builder()
                        .filename("test.txt")
                        .filePath(tempFile.toString())
                        .user(user)
                        .build();

        when(authService.getUserFromToken("token"))
                .thenReturn(user);

        when(fileRepository.findByFilenameAndUser(
                "test.txt",
                user))
                .thenReturn(Optional.of(file));

        fileService.deleteFile(
                "token",
                "test.txt"
        );

        verify(fileRepository).delete(file);
    }

    @Test
    void deleteFileNotFound() {

        UserEntity user =
                UserEntity.builder().id(1L).build();

        when(authService.getUserFromToken("token"))
                .thenReturn(user);

        when(fileRepository.findByFilenameAndUser(
                "test.txt",
                user))
                .thenReturn(Optional.empty());

        DeleteFileException ex =
                assertThrows(
                        DeleteFileException.class,
                        () -> fileService.deleteFile(
                                "token",
                                "test.txt")
                );

        assertEquals(
                "File not found",
                ex.getMessage()
        );
    }

    @Test
    void renameFile() throws Exception {

        UserEntity user =
                UserEntity.builder()
                        .id(1L)
                        .build();

        Path dir =
                Files.createDirectories(
                        Path.of("uploads", "1"));

        Path oldFile = dir.resolve("old.txt");
        Path newFile = dir.resolve("new.txt");

        Files.deleteIfExists(oldFile);
        Files.deleteIfExists(newFile);

        Files.writeString(oldFile, "data");

        FileEntity entity =
                FileEntity.builder()
                        .filename("old.txt")
                        .filePath(oldFile.toString())
                        .user(user)
                        .build();

        when(authService.getUserFromToken("token"))
                .thenReturn(user);

        when(fileRepository.findByFilenameAndUser(
                "old.txt",
                user))
                .thenReturn(Optional.of(entity));

        when(fileRepository.findByFilenameAndUser(
                "new.txt",
                user))
                .thenReturn(Optional.empty());

        fileService.renameFile(
                "token",
                "old.txt",
                "new.txt"
        );

        verify(fileRepository).save(entity);

        assertTrue(Files.exists(newFile));
        assertFalse(Files.exists(oldFile));

        Files.deleteIfExists(oldFile);
        Files.deleteIfExists(newFile);
    }

    @Test
    void renameFileNotFound() {

        UserEntity user =
                UserEntity.builder().id(1L).build();

        when(authService.getUserFromToken("token"))
                .thenReturn(user);

        when(fileRepository.findByFilenameAndUser(
                "old.txt",
                user))
                .thenReturn(Optional.empty());

        GetFileException ex =
                assertThrows(
                        GetFileException.class,
                        () -> fileService.renameFile(
                                "token",
                                "old.txt",
                                "new.txt")
                );

        assertEquals(
                "File not found",
                ex.getMessage()
        );
    }

    @Test
    void renameFileAlreadyExists() {

        UserEntity user =
                UserEntity.builder().id(1L).build();

        FileEntity oldFile =
                FileEntity.builder()
                        .filename("old.txt")
                        .user(user)
                        .build();

        FileEntity newFile =
                FileEntity.builder()
                        .filename("new.txt")
                        .user(user)
                        .build();

        when(authService.getUserFromToken("token"))
                .thenReturn(user);

        when(fileRepository.findByFilenameAndUser(
                "old.txt",
                user))
                .thenReturn(Optional.of(oldFile));

        when(fileRepository.findByFilenameAndUser(
                "new.txt",
                user))
                .thenReturn(Optional.of(newFile));

        AddingFileException ex =
                assertThrows(
                        AddingFileException.class,
                        () -> fileService.renameFile(
                                "token",
                                "old.txt",
                                "new.txt")
                );

        assertEquals(
                "File with new name already exists",
                ex.getMessage()
        );
    }

    @Test
    void listFiles() {

        UserEntity user =
                UserEntity.builder()
                        .id(1L)
                        .build();

        when(authService.getUserFromToken("token"))
                .thenReturn(user);

        when(fileRepository.findByUser(user))
                .thenReturn(List.of(
                        FileEntity.builder()
                                .filename("file1.txt")
                                .size(100L)
                                .user(user)
                                .build(),
                        FileEntity.builder()
                                .filename("file2.txt")
                                .size(200L)
                                .user(user)
                                .build()
                ));

        List<FileResponse> result =
                fileService.list("token", 10);

        assertEquals(2, result.size());
    }
}
