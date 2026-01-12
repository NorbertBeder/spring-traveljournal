package org.example.springtraveljournal.services.impl;

import org.example.springtraveljournal.models.dtos.request.JournalEntryRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.JournalEntryRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.JournalEntryResponseDto;
import org.example.springtraveljournal.models.entities.Journal;
import org.example.springtraveljournal.models.entities.JournalEntry;
import org.example.springtraveljournal.models.enums.JournalVisibility;
import org.example.springtraveljournal.repositories.JournalEntryRepository;
import org.example.springtraveljournal.repositories.JournalRepository;
import org.example.springtraveljournal.services.FriendRequestService;
import org.example.springtraveljournal.services.JournalEntryService;
import org.example.springtraveljournal.util.SecurityUtil;
import org.example.springtraveljournal.util.exceptions.ForbiddenException;
import org.example.springtraveljournal.util.exceptions.ResourceNotFoundException;
import org.example.springtraveljournal.util.mappers.JournalEntryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalEntryServiceImpl implements JournalEntryService {
    private final JournalEntryRepository journalEntryRepository;
    private final JournalRepository journalRepository;
    private final FriendRequestService friendRequestService;

    public JournalEntryServiceImpl(JournalEntryRepository journalEntryRepository, JournalRepository journalRepository, FriendRequestService friendRequestService) {
        this.journalEntryRepository = journalEntryRepository;
        this.journalRepository = journalRepository;
        this.friendRequestService = friendRequestService;
    }

    @Override
    public JournalEntryResponseDto addJournalEntry(Long journalId, JournalEntryRequestCreateDto journalEntryDto) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Journal journal = journalRepository.findById(journalId).orElseThrow(() -> new ResourceNotFoundException("Journal not found"));

        if (!journal.getOwner().getId().equals(currentUserId)) {
            throw new ForbiddenException("You are not allowed to add journal entry to this journal.");
        }

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setJournal(journal);
        journalEntry.setTitle(journalEntryDto.getTitle());
        journalEntry.setContent(journalEntryDto.getContent());

        return JournalEntryMapper.journalEntryToJournalEntryResponseDto(journalEntryRepository.save(journalEntry));
    }


    @Override
    public JournalEntryResponseDto getJournalEntry(Long journalId, Long journalEntryId) {
        Journal journal = getJournal(journalEntryId);
        assertCanViewJournal(journal);

        JournalEntry journalEntry = journalEntryRepository.findByIdAndJournalId(journalEntryId, journalId)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found"));

        return JournalEntryMapper.journalEntryToJournalEntryResponseDto(journalEntry);
    }

    @Override
    public List<JournalEntryResponseDto> getAllJournalEntries(Long journalId) {
        Journal journal = getJournal(journalId);
        assertCanViewJournal(journal);

        return journalEntryRepository.findByJournalIdOrderByEntryDateDesc(journalId)
                .stream().map(JournalEntryMapper::journalEntryToJournalEntryResponseDto).toList();
    }

    @Override
    public JournalEntryResponseDto updateJournalEntryPatch(Long journalId, Long journalEntryId, JournalEntryRequestUpdateDto journalEntryDto) {
        Journal journal = getJournal(journalId);
        assertOwnerAccess(journal);

        JournalEntry journalEntry = journalEntryRepository.findByIdAndJournalId(journalEntryId, journalId)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found"));

        if (journalEntryDto.getTitle() != null) {
            journalEntry.setTitle(journalEntryDto.getTitle());
        }
        if (journalEntryDto.getContent() != null) {
            journalEntry.setContent(journalEntryDto.getContent());
        }
        if (journalEntryDto.getEntryDate() != null) {
            journalEntry.setEntryDate(journalEntryDto.getEntryDate());
        }

        return JournalEntryMapper.journalEntryToJournalEntryResponseDto(journalEntryRepository.save(journalEntry));
    }

    @Override
    public JournalEntryResponseDto updateJournalEntryPut(Long journalId, Long journalEntryId, JournalEntryRequestCreateDto journalEntryDto) {
        Journal journal = getJournal(journalId);
        assertOwnerAccess(journal);

        JournalEntry journalEntry = journalEntryRepository.findByIdAndJournalId(journalEntryId, journalId)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found"));

        journalEntry.setTitle(journalEntryDto.getTitle());
        journalEntry.setContent(journalEntryDto.getContent());
        journalEntry.setEntryDate(journalEntryDto.getEntryDate());

        return JournalEntryMapper.journalEntryToJournalEntryResponseDto(journalEntryRepository.save(journalEntry));
    }

    @Override
    public void deleteJournalEntry(Long journalId, Long journalEntryId) {
        Journal journal = getJournal(journalId);
        assertOwnerAccess(journal);

        JournalEntry entry = journalEntryRepository.findByIdAndJournalId(journalEntryId, journalId)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found"));
        journalEntryRepository.delete(entry);
    }

    private Journal getJournal(Long journalId) {
        return journalRepository.findById(journalId)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found"));
    }

    private void assertCanViewJournal(Journal journal) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (journal.getOwner().getId().equals(currentUserId)) return;

        if (journal.getVisibility() == JournalVisibility.PUBLIC) return;

        if (journal.getVisibility() == JournalVisibility.FRIENDS) {
            if (friendRequestService.getFriendIds(currentUserId).contains(journal.getOwner().getId())) return;
        }

        throw new ForbiddenException("You are not allowed to view entries of this journal");
    }

    private void assertOwnerAccess(Journal journal) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!journal.getOwner().getId().equals(currentUserId)) {
            throw new ForbiddenException("You do not have permission to modify this journal.");
        }
    }
}
