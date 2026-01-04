package com.example.alfred.infrastructure.tts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ElevenLabsClient {

    private final List<String> apiKeys;
    private final AtomicInteger currentKeyIndex = new AtomicInteger(0);
    private final String baseUrl;

    public ElevenLabsClient(
            @Value("${app.tts.api_keys_file:keys.txt}") String apiKeysFile,
            @Value("${app.tts.base_url:https://api.elevenlabs.io/v1/tts}") String baseUrl
    ) throws IOException {
        this.apiKeys = Files.readAllLines(Paths.get(apiKeysFile))
                .stream()
                .filter(line -> !line.trim().isEmpty())
                .toList();


        if (apiKeys.isEmpty()) {
            throw new IllegalArgumentException("No API keys found in file: " + apiKeysFile);
        }

        this.baseUrl = baseUrl;
    }

    /**
     * Synthesizes speech for a given phrase using rotating API keys.
     */
    public byte[] synthesize(String phrase, String voiceId) throws IOException {
        IOException lastException = null;

        // Try each key until success
        for (int i = 0; i < apiKeys.size(); i++) {
            String apiKey = getNextApiKey();
            try {
                // 🔑 Replace with actual HTTP call to ElevenLabs API
                return callTtsApi(phrase, voiceId, apiKey);
            } catch (IOException ex) {
                lastException = ex;
                System.err.println("API key failed: " + apiKey + " → trying next key...");
            }
        }

        throw new IOException("All API keys failed for ElevenLabs request", lastException);
    }

    /**
     * Returns the next API key (round-robin).
     */
    private String getNextApiKey() {
        int index = currentKeyIndex.getAndUpdate(i -> (i + 1) % apiKeys.size());
        return apiKeys.get(index);
    }

    /**
     * Mock implementation for ElevenLabs API call.
     * Replace with your actual HTTP client logic.
     */
    private byte[] callTtsApi(String phrase, String voiceId, String apiKey) throws IOException {
        // Example with HttpClient / RestTemplate / WebClient
        // For now, mock response:
        if (Math.random() < 0.2) { // simulate some failures
            throw new IOException("Simulated API failure");
        }
        return ("FAKE_MP3_DATA_" + phrase).getBytes(); // replace with real MP3 byte[]
    }
}
