package com.backend.controller;

import com.backend.utils.UploadFile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Test {
	

    @PostMapping("/files")
    public String uploadMultipleFiles(
            @RequestPart List<MultipartFile> files,
            @RequestParam String folder) {

        if (files.isEmpty()) {
            throw new RuntimeException("No files provided for upload");
        }

        // Gọi hàm upload từ lớp UploadFile
        String urls = UploadFile.saveFiles(files, folder);

        return "Files uploaded successfully: " + urls;
    }
}
