
package com.example.File.Management.System.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;



@Entity
@Table(name = "uploaded_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", unique = true, nullable = false)
    private String fileName;

    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    public UploadedFile(String fileName) {
        this.fileName = fileName;
        this.uploadTime = LocalDateTime.now();
    }


}
