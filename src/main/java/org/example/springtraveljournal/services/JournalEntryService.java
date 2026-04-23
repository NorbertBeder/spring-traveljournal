package org.example.springtraveljournal.services;

import org.example.springtraveljournal.models.dtos.request.JournalEntryRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.JournalEntryRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.JournalEntryResponseDto;

import java.util.List;

public interface JournalEntryService {
    JournalEntryResponseDto addJournalEntry(Long journalId, JournalEntryRequestCreateDto journalEntryDto);

    JournalEntryResponseDto getJournalEntry(Long journalId, Long journalEntryId);

    List<JournalEntryResponseDto> getAllJournalEntries(Long journalId);

    JournalEntryResponseDto updateJournalEntryPatch(Long journalId, Long journalEntryId, JournalEntryRequestUpdateDto journalEntryDto);

    JournalEntryResponseDto updateJournalEntryPut(Long journalId, Long journalEntryId, JournalEntryRequestCreateDto journalEntryDto);

    void deleteJournalEntry(Long journalId, Long journalEntryId);
}
