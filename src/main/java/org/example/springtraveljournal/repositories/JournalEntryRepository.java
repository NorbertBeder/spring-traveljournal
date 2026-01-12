package org.example.springtraveljournal.repositories;

import org.example.springtraveljournal.models.entities.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByJournalIdOrderByEntryDateDesc(Long journal_id);

    Optional<JournalEntry> findByIdAndJournalId(Long entryId, Long journalId);
}
