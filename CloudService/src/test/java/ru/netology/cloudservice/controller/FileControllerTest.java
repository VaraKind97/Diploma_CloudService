package ru.netology.cloudservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudservice.dto.request.RenameRequest;
import ru.netology.cloudservice.dto.response.FileResponse;
import ru.netology.cloudservice.service.FileService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(FileController.class)
@AutoConfigureMockMvc(addFilters = false)
class FileControllerTest {

    @MockBean
    private FileService fileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void uploadFile() throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.txt",
                        "text/plain",
                        "hello".getBytes()
                );

        doNothing().when(fileService)
                .uploadFile(
                        eq("Bearer token"),
                        eq("test.txt"),
                        any()
                );

        mockMvc.perform(
                        multipart("/file")
                                .file(file)
                                .param("filename", "test.txt")
                                .header("auth-token", "Bearer token")
                )
                .andExpect(status().isOk());

        verify(fileService)
                .uploadFile(
                        eq("Bearer token"),
                        eq("test.txt"),
                        any()
                );
    }

    @Test
    void listFiles() throws Exception {

        when(fileService.list(
                "Bearer token",
                10
        )).thenReturn(
                List.of(
                        new FileResponse(
                                "file1.txt",
                                100L
                        ),
                        new FileResponse(
                                "file2.txt",
                                200L
                        )
                )
        );

        mockMvc.perform(
                        get("/list")
                                .param("limit", "10")
                                .header("auth-token", "Bearer token")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filename")
                        .value("file1.txt"))
                .andExpect(jsonPath("$[0].size")
                        .value(100))
                .andExpect(jsonPath("$[1].filename")
                        .value("file2.txt"));
    }

    @Test
    void downloadFile() throws Exception {

        byte[] content = "hello".getBytes();

        when(fileService.downloadFile(
                "Bearer token",
                "test.txt"
        )).thenReturn(content);

        mockMvc.perform(
                        get("/file")
                                .param("filename", "test.txt")
                                .header("auth-token", "Bearer token")
                )
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"test.txt\""
                ))
                .andExpect(content().bytes(content));
    }

    @Test
    void renameFile() throws Exception {

        RenameRequest request =
                new RenameRequest("new.txt");

        doNothing().when(fileService)
                .renameFile(
                        "Bearer token",
                        "old.txt",
                        "new.txt"
                );

        mockMvc.perform(
                        put("/file")
                                .param("filename", "old.txt")
                                .header("auth-token", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk());

        verify(fileService)
                .renameFile(
                        "Bearer token",
                        "old.txt",
                        "new.txt"
                );
    }

    @Test
    void deleteFile() throws Exception {

        doNothing().when(fileService)
                .deleteFile(
                        "Bearer token",
                        "test.txt"
                );

        mockMvc.perform(
                        delete("/file")
                                .param("filename", "test.txt")
                                .header("auth-token", "Bearer token")
                )
                .andExpect(status().isOk());

        verify(fileService)
                .deleteFile(
                        "Bearer token",
                        "test.txt"
                );
    }

}