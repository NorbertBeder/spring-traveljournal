package org.example.springtraveljournal.models.dtos.response;

import org.example.springtraveljournal.models.enums.JournalVisibility;

import java.time.LocalDate;

public class JournalResponseDto {
    private Long id;
    private String title;
    private String description;
    private JournalVisibility visibility;
    private LocalDate startDate;
    private LocalDate endDate;
    private String ownerEmail;
    private Long ownerId;

    public String getEmail() {
        return ownerEmail;
    }

    public void setEmail(String email) {
        this.ownerEmail = email;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JournalVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(JournalVisibility visibility) {
        this.visibility = visibility;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
