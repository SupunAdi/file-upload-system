package com.example.File.Management.System.service;


import com.example.File.Management.System.dto.FileUploadResponse;
import com.example.File.Management.System.entity.UploadedFile;
import com.example.File.Management.System.repository.UploadedFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class FileStorageServiceTest {

    @Mock
    private UploadedFileRepository repository;

    @InjectMocks
    private FileStorageService service;

    private final String tempUploadDir = "test-uploads";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new FileStorageService(repository);
        // Use reflection to set uploadDir field
        try {
            var field = FileStorageService.class.getDeclaredField("uploadDir");
            field.setAccessible(true);
            field.set(service, tempUploadDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testStoreFiles_success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "testFile.txt",
                "text/plain", "Test content".getBytes());

        when(repository.findByFileName("testFile.txt")).thenReturn(Optional.empty());
        when(repository.save(any(UploadedFile.class))).thenReturn(new UploadedFile("testFile.txt"));

        List<FileUploadResponse> responses = new ArrayList<>();
        String result = service.storeFiles(new MockMultipartFile[]{file}, responses);

        assertTrue(result.contains("All files uploaded successfully"));
        assertEquals(1, responses.size());

        // Cleanup
        Files.deleteIfExists(Paths.get(tempUploadDir).resolve("testFile.txt"));
    }

    @Test
    void testStoreFiles_duplicateFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "duplicate.txt",
                "text/plain", "Test content".getBytes());

        when(repository.findByFileName("duplicate.txt")).thenReturn(Optional.of(new UploadedFile("duplicate.txt")));

        List<FileUploadResponse> responses = new ArrayList<>();
        String result = service.storeFiles(new MockMultipartFile[]{file}, responses);

        assertTrue(result.contains("Duplicate files skipped"));
        assertEquals(0, responses.size());
    }

    @Test
    void testStoreFiles_invalidFileName() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "invalid name.txt",
                "text/plain", "Test content".getBytes());

        List<FileUploadResponse> responses = new ArrayList<>();
        String result = service.storeFiles(new MockMultipartFile[]{file}, responses);

        assertTrue(result.contains("Invalid filenames skipped"));
        assertEquals(0, responses.size());
    }

    @Test
    void testDeleteFileById_success() throws IOException {
        String fileName = "deleteMe.txt";
        Path filePath = Paths.get(tempUploadDir).resolve(fileName);
        Files.createDirectories(Paths.get(tempUploadDir));
        Files.writeString(filePath, "Dummy");

        UploadedFile fileEntity = new UploadedFile(fileName);
        fileEntity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(fileEntity));

        String result = service.deleteFileById(1L);

        assertEquals("File 'deleteMe.txt' deleted successfully.", result);
        verify(repository).deleteById(1L);
    }



    @Test
    void testGetFileById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getFileById(99L);
        });

        assertEquals("File not found with ID: 99", exception.getMessage());
    }
}