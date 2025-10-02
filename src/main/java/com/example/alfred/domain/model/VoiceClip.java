package com.example.alfred.domain.model;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "voice_clips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceClip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clipUrl;
    private String phrase;
    private String source;

    @OneToMany(mappedBy = "voiceClip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phoneme> phonemes = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void addPhoneme(Phoneme phoneme) {
        phonemes.add(phoneme);
        phoneme.setVoiceClip(this);
    }
}
