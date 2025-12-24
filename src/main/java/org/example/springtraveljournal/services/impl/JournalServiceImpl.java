package org.example.springtraveljournal.services.impl;

import org.example.springtraveljournal.models.dtos.request.JournalRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.JournalRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.JournalResponseDto;
import org.example.springtraveljournal.models.entities.Journal;
import org.example.springtraveljournal.models.entities.User;
import org.example.springtraveljournal.models.enums.JournalVisibility;
import org.example.springtraveljournal.repositories.JournalRepository;
import org.example.springtraveljournal.repositories.UserRepository;
import org.example.springtraveljournal.services.JournalService;
import org.example.springtraveljournal.services.UserService;
import org.example.springtraveljournal.util.SecurityUtil;
import org.example.springtraveljournal.util.exceptions.ResourceNotFoundException;
import org.example.springtraveljournal.util.mappers.JournalMapper;
import org.example.springtraveljournal.util.mappers.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalServiceImpl implements JournalService {

    private final JournalRepository journalRepository;
    private final UserRepository userRepository;


    public JournalServiceImpl(JournalRepository journalRepository, UserRepository userRepository, UserService userService, UserMapper userMapper) {
        this.journalRepository = journalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public JournalResponseDto createJournal(JournalRequestCreateDto journalRequest) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("No authenticated user found");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Journal journal = JournalMapper.journalRequestCreateDtoToJournal(journalRequest, user);
        journalRepository.save(journal);
        return JournalMapper.journalToJournalResponseDto(journal);
    }

    @Override
    public List<JournalResponseDto> getMyJournals() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        List<Journal> journals = journalRepository.findByOwnerId(currentUserId);
        return journals.stream().map(JournalMapper::journalToJournalResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<JournalResponseDto> getJournalsByVisibility(JournalVisibility visibility) {
        if (visibility == null) {
            throw new IllegalArgumentException("No visibility given");
        }
        List<Journal> journals = journalRepository.findByVisibility(visibility);
        return journals.stream().map(JournalMapper::journalToJournalResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<JournalResponseDto> searchJournalsWithQuery(String query) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Journal> journals = journalRepository.findByTitleContainingIgnoreCase(query)
                .stream()
                .filter(journal ->
                        journal.getOwner().getId().equals(userId)
                                || (journal.getVisibility() == JournalVisibility.PUBLIC)
                                || (journal.getVisibility()) == JournalVisibility.FRIENDS)
                .toList();
        return journals.stream().map(JournalMapper::journalToJournalResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<JournalResponseDto> getByOwnerEmail(String email) {
        List<Journal> journals = journalRepository.findByOwnerEmail(email);
        return journals.stream().map(JournalMapper::journalToJournalResponseDto).collect(Collectors.toList());
    }

    @Override
    public JournalResponseDto updateJournalAll(Long id, JournalRequestCreateDto updatedJournal) {
        Journal existingJournal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found"));

        existingJournal.setTitle(updatedJournal.getTitle());
        existingJournal.setDescription(updatedJournal.getDescription());
        existingJournal.setStartDate(updatedJournal.getStartDate());
        existingJournal.setEndDate(updatedJournal.getEndDate());
        existingJournal.setVisibility(updatedJournal.getVisibility());

        journalRepository.save(existingJournal);
        return JournalMapper.journalToJournalResponseDto(existingJournal);
    }

    public JournalResponseDto updateJournalPartial(Long id, JournalRequestUpdateDto updatedJournal) {
        Journal existingJournal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found"));

        if (updatedJournal.getTitle() != null) {
            existingJournal.setTitle(updatedJournal.getTitle());
        }
        if (updatedJournal.getDescription() != null) {
            existingJournal.setDescription(updatedJournal.getDescription());
        }
        if (updatedJournal.getStartDate() != null) {
            existingJournal.setStartDate(updatedJournal.getStartDate());
        }
        if (updatedJournal.getEndDate() != null) {
            existingJournal.setEndDate(updatedJournal.getEndDate());
        }
        if (updatedJournal.getVisibility() != null) {
            existingJournal.setVisibility(updatedJournal.getVisibility());
        }
        journalRepository.save(existingJournal);
        return JournalMapper.journalToJournalResponseDto(existingJournal);
    }

    public void deleteJournal(Long id) {
        Journal existingJournal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found"));
        journalRepository.delete(existingJournal);
    }
}
