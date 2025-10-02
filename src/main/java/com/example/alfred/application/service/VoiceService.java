package com.example.alfred.application.service;
import com.example.alfred.application.dto.VoiceResponseDto;
import java.util.concurrent.CompletableFuture;

public interface VoiceService {
    /**
     * Attempt to return a voice clip URL for the given phrase and voiceId.
     * If found in cache or DB -> return completed future with url.
     * If not found -> start generation and return future that completes with "processing" info.
     */
    CompletableFuture<VoiceResponseDto> getVoiceForPhrase(String phrase, String voiceId);
}
