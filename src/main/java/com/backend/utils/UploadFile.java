package com.backend.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UploadFile {

    private static Cloudinary cloudinary;

    @Value("${cloudinary.url}")
    public void setCloudinary(String cloudinaryUrl) {
        cloudinary = new Cloudinary(cloudinaryUrl);
    }

    public static String saveFile(MultipartFile image, String folder) {
        try {
            String uniqueFileName = UUID.randomUUID().toString();

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", uniqueFileName
            );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(image.getBytes(), uploadParams);

            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new RuntimeException("Could not save file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error during file upload: " + e.getMessage());
        }
    }

    public static String saveFiles(List<MultipartFile> images, String folder) {
        List<String> urls = new ArrayList<>();

        for (MultipartFile image : images) {
            try {
                String uniqueFileName = UUID.randomUUID().toString();

                Map<String, Object> uploadParams = ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", uniqueFileName
                );

                Map<String, Object> uploadResult = cloudinary.uploader().upload(image.getBytes(), uploadParams);
                String url = (String) uploadResult.get("url");
                urls.add(url);
            } catch (IOException e) {
                throw new RuntimeException("Could not save file: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Error during file upload: " + e.getMessage());
            }
        }

        return String.join(",", urls);
    }
}
