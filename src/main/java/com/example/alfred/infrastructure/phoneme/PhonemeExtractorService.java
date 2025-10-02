package com.example.alfred.infrastructure.phoneme;

import com.example.alfred.domain.model.Phoneme;
import com.example.alfred.domain.model.VoiceClip;
import com.example.alfred.infrastructure.persistence.PhonemeRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhonemeExtractorService {

    private final PhonemeRepository phonemeRepository;

    public PhonemeExtractorService(PhonemeRepository phonemeRepository) {
        this.phonemeRepository = phonemeRepository;
    }

    /**
     * Extract phonemes for a single word and save them to the database.
     */
    public void extractFromText(String word, VoiceClip clip) {
        List<Phoneme> phonemes = extractPhonemes(word, clip);
        phonemeRepository.saveAll(phonemes);
    }

    /**
     * Simulated phoneme extraction logic.
     * Replace this with your actual phoneme extraction library/logic.
     */
    private List<Phoneme> extractPhonemes(String word, VoiceClip clip) {
        List<Phoneme> phonemes = new ArrayList<>();
        double start = 0.0;
        double duration = 0.1; // Example fixed duration per phoneme

        for (char c : word.toCharArray()) {
            Phoneme phoneme = Phoneme.builder()
                    .symbol(String.valueOf(c))
                    .startTime(start)
                    .endTime(start + duration)
                    .voiceClip(clip)
                    .build();
            phonemes.add(phoneme);
            start += duration;
        }

        return phonemes;
    }
}
