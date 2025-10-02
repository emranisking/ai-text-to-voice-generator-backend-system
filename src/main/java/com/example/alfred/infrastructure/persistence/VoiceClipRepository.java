package com.example.alfred.infrastructure.persistence;
import com.example.alfred.domain.model.VoiceClip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoiceClipRepository extends JpaRepository<VoiceClip, Long> {
    Optional<VoiceClip> findFirstByPhrase(String phrase);

    // Add this method to support ignore case lookup
    Optional<VoiceClip> findFirstByPhraseIgnoreCase(String phrase);
}
