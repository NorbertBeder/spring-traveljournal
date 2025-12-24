package org.example.springtraveljournal.repositories;

import jakarta.validation.constraints.NotBlank;
import org.example.springtraveljournal.models.entities.Journal;
import org.example.springtraveljournal.models.entities.User;
import org.example.springtraveljournal.models.enums.JournalVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {

    Optional<Journal> findByTitle(String title);

    List<Journal> findByOwner(User owner);

    List<Journal> findByVisibility(@NotBlank JournalVisibility visibility);

    List<Journal> findByOwnerEmail(String email);

    List<Journal> findByOwnerId(Long id);

    List<Journal> findByTitleContainingIgnoreCase(String query);

}
