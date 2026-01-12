package org.example.springtraveljournal.controllers;

import jakarta.validation.Valid;
import org.example.springtraveljournal.models.dtos.request.JournalEntryRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.JournalEntryRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.JournalEntryResponseDto;
import org.example.springtraveljournal.services.JournalEntryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-journal/journals/{journalId}/entries")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    public JournalEntryController(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    @PostMapping
    public JournalEntryResponseDto addJournalEntry(@PathVariable Long journalId, @Valid @RequestBody JournalEntryRequestCreateDto journalEntryDto) {

        return journalEntryService.addJournalEntry(journalId, journalEntryDto);
    }

    @GetMapping
    public List<JournalEntryResponseDto> getAllJournalEntries(@PathVariable Long journalId) {
        return journalEntryService.getAllJournalEntries(journalId);
    }

    @GetMapping("/{journalEntryId}")
    public JournalEntryResponseDto getJournalEntry(@PathVariable Long journalId, @PathVariable Long journalEntryId) {
        return journalEntryService.getJournalEntry(journalId, journalEntryId);
    }

    @PutMapping("/{journalEntryId}")
    public JournalEntryResponseDto updateJournalEntryPut(@PathVariable Long journalId, @PathVariable Long journalEntryId, @Valid @RequestBody JournalEntryRequestCreateDto journalEntryDto) {
        return journalEntryService.updateJournalEntryPut(journalId, journalEntryId, journalEntryDto);
    }

    @PatchMapping("/{journalEntryId}")
    public JournalEntryResponseDto updateJournalEntryPatch(@PathVariable Long journalId, @PathVariable Long journalEntryId, @RequestBody JournalEntryRequestUpdateDto journalEntryDto) {
        return journalEntryService.updateJournalEntryPatch(journalId, journalEntryId, journalEntryDto);
    }

    @DeleteMapping("/{journalEntryId}")
    public void deleteJournalEntry(@PathVariable Long journalId, @PathVariable Long journalEntryId) {
        journalEntryService.deleteJournalEntry(journalId, journalEntryId);
    }

}
