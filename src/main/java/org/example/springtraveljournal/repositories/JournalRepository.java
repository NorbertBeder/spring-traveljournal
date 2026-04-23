package org.example.springtraveljournal.repositories;

import jakarta.validation.constraints.NotBlank;
import org.example.springtraveljournal.models.entities.Journal;
import org.example.springtraveljournal.models.enums.JournalVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {

    List<Journal> findByVisibility(@NotBlank JournalVisibility visibility);

    List<Journal> findByOwnerEmail(String email);

    List<Journal> findByOwnerId(Long id);

    List<Journal> findByTitleContainingIgnoreCase(String query);

}
