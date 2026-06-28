package ru.netology.cloudservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.dto.request.RenameRequest;
import ru.netology.cloudservice.dto.response.FileResponse;
import ru.netology.cloudservice.service.FileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {

    private static final Logger log =
            LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename,
            @RequestParam("file") MultipartFile file) {

        log.info(
                "Upload request: {}",
                filename
        );

        fileService.uploadFile(token, filename, file);

        log.info(
                "File '{}' uploaded successfully",
                filename
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public List<FileResponse> list(
            @RequestHeader("auth-token") String token,
            @RequestParam(defaultValue = "10") Integer limit) {

        log.info(
                "Request file list, limit={}",
                limit
        );

        return fileService.list(token, limit);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> getFileInfo(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename) {

        log.info(
                "Download request: {}",
                filename
        );

        byte[] data = fileService.downloadFile(token, filename);

        log.info(
                "File '{}' downloaded",
                filename
        );

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=\"" + filename + "\""
                )
                .body(data);
    }

    @PutMapping("/file")
    public ResponseEntity<Void> updateFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename,
            @RequestBody RenameRequest request) {

        log.info(
                "Rename request: '{}' -> '{}'",
                filename,
                request.filename()
        );

        fileService.renameFile(
                token,
                filename,
                request.filename()
        );

        log.info("File successfully renamed");

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename) {

        log.info(
                "Delete request: {}",
                filename
        );

        fileService.deleteFile(token, filename);

        log.info(
                "File '{}' deleted",
                filename
        );

        return ResponseEntity.ok().build();
    }
}