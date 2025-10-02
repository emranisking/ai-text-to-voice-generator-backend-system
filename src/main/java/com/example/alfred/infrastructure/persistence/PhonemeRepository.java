package com.example.alfred.infrastructure.persistence;
import com.example.alfred.domain.model.Phoneme;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PhonemeRepository extends JpaRepository<Phoneme, Long> {
    Optional<Phoneme> findFirstBySymbol(String symbol);
}
