package org.example.springtraveljournal.util.mappers;

import org.example.springtraveljournal.models.dtos.request.JournalRequestCreateDto;
import org.example.springtraveljournal.models.dtos.response.JournalResponseDto;
import org.example.springtraveljournal.models.entities.Journal;
import org.example.springtraveljournal.models.entities.User;

public class JournalMapper {
    public static JournalResponseDto journalToJournalResponseDto(Journal journal) {
        JournalResponseDto dto = new JournalResponseDto();
        dto.setId(journal.getId());
        dto.setTitle(journal.getTitle());
        dto.setDescription(journal.getDescription());
        dto.setVisibility(journal.getVisibility());
        dto.setStartDate(journal.getStartDate());
        dto.setEndDate(journal.getEndDate());
        dto.setOwnerId(journal.getOwner().getId());
        return dto;
    }

    public static Journal journalRequestCreateDtoToJournal(JournalRequestCreateDto dto, User owner) {
        Journal journal = new Journal();
        journal.setOwner(owner);
        journal.setTitle(dto.getTitle());
        journal.setDescription(dto.getDescription());
        journal.setVisibility(dto.getVisibility());
        journal.setStartDate(dto.getStartDate());
        journal.setEndDate(dto.getEndDate());
        return journal;
    }
}
