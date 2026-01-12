package org.example.springtraveljournal.util.mappers;

import org.example.springtraveljournal.models.dtos.response.JournalEntryResponseDto;
import org.example.springtraveljournal.models.entities.JournalEntry;

public class JournalEntryMapper {

    public static JournalEntryResponseDto journalEntryToJournalEntryResponseDto(JournalEntry journalEntry) {
        JournalEntryResponseDto dto = new JournalEntryResponseDto();
        
        dto.setId(journalEntry.getId());
        dto.setTitle(journalEntry.getTitle());
        dto.setContent(journalEntry.getContent());
        dto.setEntryDate(journalEntry.getEntryDate());
        dto.setCreatedAt(journalEntry.getCreatedAt());
        return dto;
    }
}
