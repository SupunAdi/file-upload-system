package com.example.File.Management.System.controller;

import com.example.File.Management.System.dto.FileUploadResponse;
import com.example.File.Management.System.entity.UploadedFile;
import com.example.File.Management.System.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FileUploadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileStorageService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new FileUploadController(service)).build();
    }

    @Test
    void testUploadFiles_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "test.txt", "text/plain", "hello".getBytes());

        FileUploadResponse response = new FileUploadResponse(1L, "test.txt");
        when(service.storeFiles(any(), any())).thenAnswer(invocation -> {
            List<FileUploadResponse> list = invocation.getArgument(1);
            list.add(response);
            return "All files uploaded successfully.";
        });

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploaded[0].fileName").value("test.txt"))
                .andExpect(jsonPath("$.message").value("All files uploaded successfully."));
    }

    @Test
    void testDeleteFile_success() throws Exception {
        when(service.deleteFileById(1L)).thenReturn("File 'test.txt' deleted successfully.");

        mockMvc.perform(delete("/api/files/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File 'test.txt' deleted successfully."));
    }

    @Test
    void testDeleteFile_notFound() throws Exception {
        when(service.deleteFileById(999L)).thenThrow(new IllegalArgumentException("File not found with ID: 999"));

        mockMvc.perform(delete("/api/files/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("File not found with ID: 999"));
    }

    @Test
    void testGetFileById_success() throws Exception {
        UploadedFile file = new UploadedFile("demo.txt");
        file.setId(10L);

        when(service.getFileById(10L)).thenReturn(file);

        mockMvc.perform(get("/api/files/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("demo.txt"));
    }

    @Test
    void testListAllFiles() throws Exception {
        UploadedFile file = new UploadedFile("file1.txt");
        file.setId(1L);

        when(service.getAllFiles()).thenReturn(List.of(file));

        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName").value("file1.txt"));
    }
}