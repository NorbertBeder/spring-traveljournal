package org.example.springtraveljournal.controllers;

import jakarta.validation.Valid;
import org.example.springtraveljournal.models.dtos.request.JournalRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.JournalRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.JournalResponseDto;
import org.example.springtraveljournal.models.enums.JournalVisibility;
import org.example.springtraveljournal.services.JournalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-journal/journals")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @PostMapping
    public JournalResponseDto createJournal(@Valid @RequestBody JournalRequestCreateDto journalRequest) {
        return journalService.createJournal(journalRequest);
    }


    @GetMapping("/{visibility}")
    public List<JournalResponseDto> getJournalsByVisibility(@PathVariable JournalVisibility visibility) {
        return journalService.getJournalsByVisibility(visibility);
    }

    @GetMapping("/my-journals")
    public List<JournalResponseDto> getMyJournals() {
        return journalService.getMyJournals();
    }

    @GetMapping("/search")
    public List<JournalResponseDto> searchJournalsWithQuery(@RequestParam String query) {
        return journalService.searchJournalsWithQuery(query);
    }

    @PutMapping("/update/{id}")
    public JournalResponseDto updateJournalAll(@PathVariable Long id, @RequestBody JournalRequestCreateDto journalRequest) {
        return journalService.updateJournalAll(id, journalRequest);
    }

    @PatchMapping("/update/{id}")
    public JournalResponseDto updateJournalPartial(@PathVariable Long id, @RequestBody JournalRequestUpdateDto journal) {
        return journalService.updateJournalPartial(id, journal);
    }

    @DeleteMapping("/{id}")
    public void deleteJournal(@PathVariable Long id) {
        journalService.deleteJournal(id);
    }
}
