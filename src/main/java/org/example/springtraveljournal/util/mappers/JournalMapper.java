package org.example.springtraveljournal.util.mappers;

import org.example.springtraveljournal.models.dtos.response.JournalResponseDto;
import org.example.springtraveljournal.models.entities.Journal;
import org.springframework.stereotype.Component;

@Component
public class JournalMapper {
    public JournalResponseDto journalToJournalResponseDto(Journal journal) {
        JournalResponseDto dto = new JournalResponseDto();
        dto.setId(journal.getId());
        dto.setTitle(journal.getTitle());
        dto.setDescription(journal.getDescription());
        dto.setVisibility(journal.getVisibility());
        dto.setStartDate(journal.getStartDate());
        dto.setEndDate(journal.getEndDate());
        dto.setEmail(journal.getOwner().getEmail());
        dto.setOwnerId(journal.getOwner().getId());
        return dto;
    }
}
