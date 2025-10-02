package com.example.alfred.infrastructure.storage;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class FileStorageService {

    private final Path basePath = Paths.get("./voice_db");

    public FileStorageService() throws IOException {
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
        }
    }

    // Save MP3 file and return its path
    public String saveMp3(String phrase, byte[] mp3Bytes) throws IOException {
        String safePhrase = phrase.replaceAll("[^a-zA-Z0-9]", "_");
        long timestamp = System.currentTimeMillis();
        String filename = safePhrase + "-" + timestamp + ".mp3";
        Path filePath = basePath.resolve(filename);
        Files.write(filePath, mp3Bytes, StandardOpenOption.CREATE_NEW);
        return filePath.toAbsolutePath().toString();
    }

    // Load MP3 bytes from a path
    public byte[] loadMp3(String filePath) throws IOException {
        // Remove "file:///" if present
        if (filePath.startsWith("file:///")) {
            filePath = filePath.substring(8);
        }
        return Files.readAllBytes(Paths.get(filePath));
    }

    // Merge multiple MP3 byte arrays into one
    public byte[] mergeMp3Bytes(List<byte[]> mp3BytesList) throws IOException {
        try (var outputStream = new java.io.ByteArrayOutputStream()) {
            for (byte[] bytes : mp3BytesList) {
                outputStream.write(bytes);
            }
            return outputStream.toByteArray();
        }
    }
}
