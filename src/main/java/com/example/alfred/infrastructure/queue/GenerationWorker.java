package com.example.alfred.infrastructure.queue;

import com.example.alfred.domain.model.VoiceClip;
import com.example.alfred.infrastructure.persistence.VoiceClipRepository;
import com.example.alfred.infrastructure.phoneme.PhonemeExtractorService;
import com.example.alfred.infrastructure.storage.FileStorageService;
import com.example.alfred.infrastructure.tts.ElevenLabsClient;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class GenerationWorker {

    private final ExecutorService executor;
    private final ElevenLabsClient ttsClient;
    private final FileStorageService storageService;
    private final VoiceClipRepository clipRepository;
    private final PhonemeExtractorService phonemeExtractor;
    private final String voiceId;

    public GenerationWorker(ElevenLabsClient ttsClient,
                            FileStorageService storageService,
                            VoiceClipRepository clipRepository,
                            PhonemeExtractorService phonemeExtractor,
                            @Value("${app.tts.voice_id:default_voice}") String voiceId) {
        this.ttsClient = ttsClient;
        this.storageService = storageService;
        this.clipRepository = clipRepository;
        this.phonemeExtractor = phonemeExtractor;
        this.voiceId = voiceId;
        this.executor = Executors.newFixedThreadPool(6);
    }

    /**
     * Enqueue phrase generation. Reuses DB audio if exists, otherwise calls API.
     */
    public void enqueueGeneration(String phrase) {
        executor.submit(() -> {
            try {
                List<byte[]> mp3List = new ArrayList<>();

                // Step 1: Check if phrase already exists
                Optional<VoiceClip> existingClip = clipRepository.findFirstByPhraseIgnoreCase(phrase);

                if (existingClip.isPresent()) {
                    mp3List.add(storageService.loadMp3(existingClip.get().getClipUrl()));
                } else {
                    // Step 2: Generate new voice
                    byte[] newMp3 = ttsClient.synthesize(phrase, voiceId);
                    String newMp3Path = storageService.saveMp3(phrase, newMp3);
                    mp3List.add(newMp3);

                    VoiceClip newClip = VoiceClip.builder()
                            .phrase(phrase)
                            .clipUrl(newMp3Path)
                            .source("API")
                            .build();
                    clipRepository.save(newClip);
                }

                // Step 3: Merge MP3s
                if (!mp3List.isEmpty()) {
                    byte[] mergedMp3 = storageService.mergeMp3Bytes(mp3List);
                    String mergedPath = storageService.saveMp3(phrase + "_merged", mergedMp3);

                    VoiceClip clip = VoiceClip.builder()
                            .phrase(phrase)
                            .clipUrl(mergedPath)
                            .source("MERGED")
                            .build();
                    clipRepository.save(clip);

                    // Step 4: Extract phonemes
                    phonemeExtractor.extractFromText(phrase, clip);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void queue(String text, String voiceId, String filename) {
        enqueueGeneration(text); // reuse your existing logic
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}