//
//import jakarta.persistence.*;
//import lombok.Data;
//
//@Entity
//@Data
//
//
//public class UploadedFile {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long fileid;
//
//    private String fileName;
//
//    private String fileType;
//
//    @Lob
//    @Column(length = 100000)
//    private String content;
//}
//
package com.example.File.Management.System.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

//    public UploadedFile(String originalName) {
//    }

    public UploadedFile(String fileName) {
        this.fileName = fileName;
    }

}
