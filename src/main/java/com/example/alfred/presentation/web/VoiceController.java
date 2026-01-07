package com.example.alfred.presentation.web;

import com.example.alfred.application.service.RedisService;
import com.example.alfred.infrastructure.queue.GenerationWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.alfred.application.dto.VoiceResponseDto;

@RestController
@RequestMapping("/real")
public class VoiceController {


    private final RedisService redisService;
    private final GenerationWorker generationWorker;

    public VoiceController(RedisService redisService, GenerationWorker generationWorker) {
        this.redisService = redisService;
        this.generationWorker = generationWorker;
    }


    @GetMapping("/voice")
    public ResponseEntity<VoiceResponseDto> generateVoice(
            @RequestParam(name = "text") String text,
            @RequestParam(name = "voiceId", defaultValue = "default") String voiceId) {

        String filename = redisService.get(text);
        if (filename != null) {
            String clipUrl = "https://71b2086611f8.ngrok-free.app/voice_db/" + filename;
            return ResponseEntity.ok(new VoiceResponseDto("HIT", clipUrl, "returned from redis cache"));
        }

        filename = text + "-" + System.currentTimeMillis() + ".mp3";

        // 🔥 This is where GenerationWorker is called
        generationWorker.queue(text, voiceId, filename);

        redisService.set(text, filename);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new VoiceResponseDto("PROCESSING", null, "voice is being generated"));
    }
}