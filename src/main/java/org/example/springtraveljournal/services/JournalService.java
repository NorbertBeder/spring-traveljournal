package org.example.springtraveljournal.services;

import org.example.springtraveljournal.models.dtos.request.JournalRequestUpdateDto;
import org.example.springtraveljournal.models.entities.Journal;

import java.util.List;


public interface JournalService {

    Journal createJournal(Journal journal);

    List<Journal> getAllJournals();

    Journal getJournalById(Long id);

    List<Journal> getByOwnerId(Long userId);

    List<Journal> getByOwnerEmail(String email);

    Journal updateJournalAll(Long id, JournalRequestUpdateDto updatedJournal);

    Journal updateJournalPartial(Long id, JournalRequestUpdateDto updatedJournal);

    void deleteJournal(Long id);
}
