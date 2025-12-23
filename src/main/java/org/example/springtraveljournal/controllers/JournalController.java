package org.example.springtraveljournal.controllers;

import jakarta.validation.Valid;
import org.example.springtraveljournal.models.dtos.request.JournalRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.JournalRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.JournalResponseDto;
import org.example.springtraveljournal.models.entities.Journal;
import org.example.springtraveljournal.models.entities.User;
import org.example.springtraveljournal.services.JournalService;
import org.example.springtraveljournal.services.UserService;
import org.example.springtraveljournal.util.SecurityUtil;
import org.example.springtraveljournal.util.mappers.JournalMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-journal/journals")
public class JournalController {

    private final JournalService journalService;
    private final UserService userService;
    private final JournalMapper journalMapper;

    public JournalController(JournalService journalService, UserService userService, JournalMapper journalMapper) {
        this.journalService = journalService;
        this.userService = userService;
        this.journalMapper = journalMapper;
    }

    @PostMapping
    public JournalResponseDto createJournal(@Valid @RequestBody JournalRequestCreateDto journalRequest) {
        Long userId = SecurityUtil.getCurrentUserId();

        User user = userService.getUser(userId);

        Journal journal = new Journal();

        journal.setTitle(journalRequest.getTitle());
        journal.setDescription(journalRequest.getDescription());
        journal.setVisibility(journalRequest.getVisibility());
        journal.setStartDate(journalRequest.getStartDate());
        journal.setEndDate(journalRequest.getEndDate());
        journal.setOwner(user);

        Journal createdJournal = journalService.createJournal(journal);
        return journalMapper.journalToJournalResponseDto(createdJournal);
    }

//    @GetMapping
//    public List<Journal> getJournals() {
//        return journalService.getAllJournals();
//    }
//
//    @GetMapping("/{id}")
//    public Journal getJournal(@PathVariable Long id) {
//        return journalService.getJournalById(id);
//    }
//
//    @GetMapping("/user/{userId}")
//    public List<Journal> getJournalsByUserId(@PathVariable Long userId) {
//        return journalService.getByOwnerId(userId);
//    }
//
//    @GetMapping("/user/email")
//    public List<Journal> getJournalsByUserEmail(@RequestParam String email) {
//        return journalService.getByOwnerEmail(email);
//    }
//
//    @PutMapping("/{id}")
//    public Journal updateJournalAll(@PathVariable Long id, @Valid @RequestBody JournalRequestUpdateDto journalRequest) {
//        return journalService.updateJournalAll(id, journalRequest);
//    }
//
//    @PatchMapping("/{id}")
//    public Journal updateJournalPartial(@PathVariable Long id, @RequestBody JournalRequestUpdateDto journal) {
//        return journalService.updateJournalPartial(id, journal);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteJournal(@PathVariable Long id) {
//        journalService.deleteJournal(id);
//    }
}
