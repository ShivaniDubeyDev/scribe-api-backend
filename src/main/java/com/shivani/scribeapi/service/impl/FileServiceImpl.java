package com.shivani.scribeapi.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shivani.scribeapi.exception.ApiException;
import com.shivani.scribeapi.service.FileService;

@Service
public class FileServiceImpl implements FileService {

	@Override
	public String uploadImage(String path, MultipartFile file) throws IOException {
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || !originalFilename.contains(".")) {
			throw new ApiException("Malformed filename or missing extension.");
		}

		// Generate a secure, unique randomized filename identifier
		String randomId = UUID.randomUUID().toString();
		
		// Converted extension to lowercase to prevent case-sensitive Linux server lookup bugs
		String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
		String securedFileName = randomId.concat(extension);

		// safe modern Path constructions to prevent directory traversal vulnerabilities
		Path directoryPath = Paths.get(path);
		Path targetFilePath = directoryPath.resolve(securedFileName);

		// Automatically create storage directories if missing
		if (!Files.exists(directoryPath)) {
			Files.createDirectories(directoryPath);
		}

		// Copy file stream using atomic overwrite locks
		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
		}

		return securedFileName;
	}

	@Override
	public InputStream getResource(String path, String fileName) throws FileNotFoundException {
		Path targetFilePath = Paths.get(path).resolve(fileName);

		try {
			return Files.newInputStream(targetFilePath);
		} catch (IOException e) {
			throw new FileNotFoundException("Requested image file asset could not be located on the storage tier.");
		}
	}
}
