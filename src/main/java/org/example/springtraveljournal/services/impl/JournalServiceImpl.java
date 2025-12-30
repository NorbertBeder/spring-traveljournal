package org.example.springtraveljournal.services.impl;

import org.example.springtraveljournal.models.dtos.request.JournalRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.JournalRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.JournalResponseDto;
import org.example.springtraveljournal.models.entities.Journal;
import org.example.springtraveljournal.models.entities.User;
import org.example.springtraveljournal.models.enums.JournalVisibility;
import org.example.springtraveljournal.repositories.JournalRepository;
import org.example.springtraveljournal.repositories.UserRepository;
import org.example.springtraveljournal.services.FriendRequestService;
import org.example.springtraveljournal.services.JournalService;
import org.example.springtraveljournal.services.UserService;
import org.example.springtraveljournal.util.SecurityUtil;
import org.example.springtraveljournal.util.exceptions.ForbiddenException;
import org.example.springtraveljournal.util.exceptions.ResourceNotFoundException;
import org.example.springtraveljournal.util.mappers.JournalMapper;
import org.example.springtraveljournal.util.mappers.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalServiceImpl implements JournalService {

    private final JournalRepository journalRepository;
    private final UserRepository userRepository;
    private final FriendRequestService friendRequestService;

    public JournalServiceImpl(JournalRepository journalRepository, UserRepository userRepository, UserService userService, UserMapper userMapper, FriendRequestService friendRequestService) {
        this.journalRepository = journalRepository;
        this.userRepository = userRepository;
        this.friendRequestService = friendRequestService;
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
    @Transactional(readOnly = true)
    public List<JournalResponseDto> getJournalsByVisibility(JournalVisibility visibility) {

        if (visibility == null) {
            throw new IllegalArgumentException("No visibility given");
        }

        Long userId = SecurityUtil.getCurrentUserId();
        List<Long> friendIds = friendRequestService.getFriendIds(userId);

        List<Journal> journals = journalRepository.findByVisibility(visibility)
                .stream()
                .filter(journal -> switch (visibility) {

                    case PUBLIC -> true;

                    case FRIENDS -> friendIds.contains(journal.getOwner().getId());

                    case PRIVATE -> journal.getOwner().getId().equals(userId);
                })
                .toList();

        return journals.stream()
                .map(JournalMapper::journalToJournalResponseDto)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<JournalResponseDto> searchJournalsWithQuery(String query) {

        if (query == null || query.isBlank()) {
            return List.of();
        }

        String normalizedQuery = query.trim();
        Long userId = SecurityUtil.getCurrentUserId();
        List<Long> friendIds = friendRequestService.getFriendIds(userId);

        List<Journal> journals = journalRepository
                .findByTitleContainingIgnoreCase(normalizedQuery)
                .stream()
                .filter(journal ->
                        journal.getVisibility() == JournalVisibility.PUBLIC
                                || (journal.getVisibility() == JournalVisibility.FRIENDS
                                && friendIds.contains(journal.getOwner().getId()))
                )
                .toList();

        return journals.stream()
                .map(JournalMapper::journalToJournalResponseDto)
                .toList();
    }

    @Override
    public List<JournalResponseDto> getByOwnerEmail(String email) {
        List<Journal> journals = journalRepository.findByOwnerEmail(email);
        return journals.stream().map(JournalMapper::journalToJournalResponseDto).collect(Collectors.toList());
    }

    @Override
    public JournalResponseDto updateJournalAll(Long id, JournalRequestCreateDto updatedJournal) {
        Journal existingJournal = getOwnedJournalOrThrow(id);

        existingJournal.setTitle(updatedJournal.getTitle());
        existingJournal.setDescription(updatedJournal.getDescription());
        existingJournal.setStartDate(updatedJournal.getStartDate());
        existingJournal.setEndDate(updatedJournal.getEndDate());
        existingJournal.setVisibility(updatedJournal.getVisibility());

        journalRepository.save(existingJournal);
        return JournalMapper.journalToJournalResponseDto(existingJournal);
    }

    public JournalResponseDto updateJournalPartial(Long id, JournalRequestUpdateDto updatedJournal) {
        Journal existingJournal = getOwnedJournalOrThrow(id);

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
        Journal existingJournal = getOwnedJournalOrThrow(id);

        journalRepository.delete(existingJournal);
    }

    private Journal getOwnedJournalOrThrow(Long journalId) {

        Long userId = SecurityUtil.getCurrentUserId();

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found"));

        if (!journal.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to modify this journal");
        }
        return journal;
    }
}
