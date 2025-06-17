package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service implementation for handling file operations, such as uploading images.
 * This service generates a unique name for the uploaded files to avoid filename conflicts.
 *
 * @author Adithya R
 */
@Service
public class FileServiceImpl implements FileService {

    /**
     * Uploads an image to the specified directory.
     * It generates a unique name for the uploaded file to avoid name conflicts
     * and stores the file in the provided directory. If the directory doesn't exist,
     * it will be created.
     *
     * @param path The directory path where the image will be stored.
     * @param file The file that is uploaded.
     * @return The unique name of the uploaded file.
     * @throws IOException If an error occurs while copying the file.
     */
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // Get the original file name
        String originalFileName = file.getOriginalFilename();

        // Generate a unique file name using UUID to prevent conflicts
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));

        // Define the complete path where the file will be stored
        String filePath = path + File.separator + fileName;

        // Create the directory if it doesn't already exist
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Copy the file to the defined path
        Files.copy(file.getInputStream(), Paths.get(filePath));

        // Return the unique file name
        return fileName;
    }
}
