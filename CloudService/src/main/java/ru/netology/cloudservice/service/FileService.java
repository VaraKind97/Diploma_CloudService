package ru.netology.cloudservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.netology.cloudservice.dto.response.FileResponse;
import ru.netology.cloudservice.exceptions.AddingFileException;
import ru.netology.cloudservice.exceptions.DeleteFileException;
import ru.netology.cloudservice.exceptions.GetFileException;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.UserEntity;
import ru.netology.cloudservice.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FileService {

    private static final Logger log =
            LoggerFactory.getLogger(FileService.class);

    private final FileRepository fileRepository;
    private final AuthService authService;

    public FileService(FileRepository fileRepository,
                       AuthService authService) {
        this.fileRepository = fileRepository;
        this.authService = authService;
    }

    public void uploadFile(String token,
                           String filename,
                           MultipartFile file) {

        UserEntity user = authService.getUserFromToken(token);

        log.info(
                "Uploading file '{}' for user '{}'",
                filename,
                user.getUsername()
        );

        if (fileRepository.findByFilenameAndUser(filename, user).isPresent()) {
            throw new AddingFileException("File already exists");
        }

        try {
            Path userDir = Path.of("uploads", String.valueOf(user.getId()));
            Files.createDirectories(userDir);

            Path target = userDir.resolve(filename);
            file.transferTo(target);

            FileEntity entity = FileEntity.builder()
                    .filename(filename)
                    .filePath(target.toString())
                    .size(file.getSize())
                    .user(user)
                    .build();

            fileRepository.save(entity);

            log.info(
                    "File '{}' uploaded successfully",
                    filename
            );

        } catch (IOException e) {
            log.error(
                    "Error uploading file '{}'",
                    filename,
                    e
            );
            throw new AddingFileException("Error upload file");
        }
    }

    public byte[] downloadFile(String token, String filename) {
        UserEntity user = authService.getUserFromToken(token);

        log.info(
                "Downloading file '{}' for user '{}'",
                filename,
                user.getUsername()
        );

        FileEntity file = fileRepository.findByFilenameAndUser(filename, user)
                .orElseThrow(() ->
                        new GetFileException("File not found"));

        try {
            return Files.readAllBytes(Path.of(file.getFilePath()));
        } catch (IOException e) {
            log.error(
                    "Error downloading file '{}'",
                    filename,
                    e
            );
            throw new GetFileException("Error download file");
        }
    }

    public void deleteFile(String token, String filename) {
        UserEntity user = authService.getUserFromToken(token);
        log.info(
                "Deleting file '{}' for user '{}'",
                filename,
                user.getUsername()
        );
        FileEntity file = fileRepository.findByFilenameAndUser(filename, user)
                .orElseThrow(() ->
                        new DeleteFileException("File not found"));

        try {
            Files.deleteIfExists(Path.of(file.getFilePath()));
        } catch (IOException e) {
            log.error(
                    "Error deleting file '{}'",
                    filename,
                    e
            );
            throw new DeleteFileException("Error delete file");
        }

        fileRepository.delete(file);

        log.info(
                "File '{}' deleted",
                filename
        );
    }

    public void renameFile(String token, String filename, String newFilename) {

        UserEntity user = authService.getUserFromToken(token);
        log.info(
                "Renaming file '{}' to '{}' for user '{}'",
                filename,
                newFilename,
                user.getUsername()
        );

        FileEntity fileEntity = fileRepository
                .findByFilenameAndUser(filename, user)
                .orElseThrow(() ->
                        new GetFileException("File not found"));

        if (fileRepository.findByFilenameAndUser(newFilename, user).isPresent()) {
            throw new AddingFileException("File with new name already exists");
        }

        Path userDir = Path.of("uploads", String.valueOf(user.getId()));

        Path oldPath = userDir.resolve(filename);
        Path newPath = userDir.resolve(newFilename);

        if (!Files.exists(oldPath)) {
            throw new GetFileException("Physical file not found");
        }

        try {
            Files.move(
                    oldPath,
                    newPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            log.error(
                    "Error renaming file '{}' to '{}'",
                    filename,
                    newFilename,
                    e
            );
            throw new AddingFileException("Rename file error");
        }

        fileEntity.setFilename(newFilename);
        fileEntity.setFilePath(newPath.toString());

        fileRepository.save(fileEntity);

        log.info(
                "File '{}' successfully renamed to '{}'",
                filename,
                newFilename
        );
    }

    public List<FileResponse> list(String token, Integer limit) {

        UserEntity user = authService.getUserFromToken(token);

        log.debug(
                "Loading file list for user '{}', limit={}",
                user.getUsername(),
                limit
        );

        return fileRepository.findByUser(user)
                .stream()
                .limit(limit)
                .map(file -> new FileResponse(
                        file.getFilename(),
                        file.getSize()
                ))
                .toList();
    }
}

