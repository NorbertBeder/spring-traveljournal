package org.example.springtraveljournal.services;

import org.example.springtraveljournal.models.dtos.request.JournalRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.JournalRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.JournalResponseDto;
import org.example.springtraveljournal.models.enums.JournalVisibility;

import java.util.List;


public interface JournalService {

    JournalResponseDto createJournal(JournalRequestCreateDto journalRequest);

    List<JournalResponseDto> getMyJournals();

    List<JournalResponseDto> getJournalsByVisibility(JournalVisibility visibility);

    List<JournalResponseDto> searchJournalsWithQuery(String query);

    JournalResponseDto updateJournalAll(Long id, JournalRequestCreateDto journalRequest);

    JournalResponseDto updateJournalPartial(Long id, JournalRequestUpdateDto updatedJournal);

    void deleteJournal(Long id);
}
