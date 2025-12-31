package com.example.alfred.application.service.impl;
import com.example.alfred.application.dto.VoiceResponseDto;
import com.example.alfred.application.service.VoiceService;
import com.example.alfred.domain.model.VoiceClip;
import com.example.alfred.infrastructure.persistence.VoiceClipRepository;
import com.example.alfred.infrastructure.queue.GenerationWorker;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Builder
public class VoiceServiceImpl implements VoiceService {

    private final VoiceClipRepository clipRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final GenerationWorker worker;

    @Autowired
    public VoiceServiceImpl(VoiceClipRepository clipRepository,
                            RedisTemplate<String, String> redisTemplate,
                            GenerationWorker worker) {
        this.clipRepository = clipRepository;
        this.redisTemplate = redisTemplate;
        this.worker = worker;
    }


    private static String redisKey(String phrase, String voiceId) {
        return "phrase:" + voiceId + ":" + phrase.toLowerCase().trim();
    }


    @Override
    public CompletableFuture<VoiceResponseDto> getVoiceForPhrase(String phrase, String voiceId) {
        return CompletableFuture.supplyAsync(() -> {
            // 1) Check Redis cache
            String key = redisKey(phrase, voiceId);
            String cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return VoiceResponseDto.builder()
                        .status("HIT")
                        .clipUrl(cached)
                        .message("returned from redis cache")
                        .build();
            }

            // 2) Check Postgres
            Optional<VoiceClip> clipOpt = clipRepository.findFirstByPhraseIgnoreCase(phrase);
            if (clipOpt.isPresent()) {
                VoiceClip clip = clipOpt.get();
                // update redis and return
                redisTemplate.opsForValue().set(key, clip.getClipUrl());
                return VoiceResponseDto.builder()
                        .status("HIT")
                        .clipUrl(clip.getClipUrl())
                        .message("returned from postgres")
                        .build();
            }

            // 3) Miss: enqueue generation and return processing response
            worker.enqueueGeneration(phrase);
            return VoiceResponseDto.builder()
                    .status("PROCESSING")
                    .clipUrl(null)
                    .message("generation enqueued; please poll later or subscribe to notifications")
                    .build();
        });
    }
}
