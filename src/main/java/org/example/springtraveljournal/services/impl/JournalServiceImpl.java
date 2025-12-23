package org.example.springtraveljournal.services.impl;

import org.example.springtraveljournal.models.dtos.request.JournalRequestUpdateDto;
import org.example.springtraveljournal.models.entities.Journal;
import org.example.springtraveljournal.repositories.JournalRepository;
import org.example.springtraveljournal.services.JournalService;
import org.example.springtraveljournal.util.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalServiceImpl implements JournalService {

    private final JournalRepository journalRepository;

    public JournalServiceImpl(JournalRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    public Journal createJournal(Journal journal) {
        return journalRepository.save(journal);
    }

    public List<Journal> getAllJournals() {
        return journalRepository.findAll();
    }

    public Journal getJournalById(Long id) {
        return journalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Journal not found"));
    }

    public List<Journal> getByOwnerId(Long userId) {
        return journalRepository.findByOwnerId(userId);
    }

    public List<Journal> getByOwnerEmail(String email) {
        return journalRepository.findByOwnerEmail(email);
    }

    public Journal updateJournalAll(Long id, JournalRequestUpdateDto updatedJournal) {
        Journal existingJournal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found"));

        existingJournal.setTitle(updatedJournal.getTitle());
        existingJournal.setDescription(updatedJournal.getDescription());
        existingJournal.setStartDate(updatedJournal.getStartDate());
        existingJournal.setEndDate(updatedJournal.getEndDate());
        existingJournal.setVisibility(updatedJournal.getVisibility());

        return journalRepository.save(existingJournal);
    }

    public Journal updateJournalPartial(Long id, JournalRequestUpdateDto updatedJournal) {
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

        return journalRepository.save(existingJournal);
    }

    public void deleteJournal(Long id) {
        Journal existingJournal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found"));
        journalRepository.delete(existingJournal);
    }
}
