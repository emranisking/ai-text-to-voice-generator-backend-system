package com.example.alfred.domain.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phonemes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Phoneme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String symbol;

    private double startTime;
    private double endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voice_clip_id")
    private VoiceClip voiceClip;
}
