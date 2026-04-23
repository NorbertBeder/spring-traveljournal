package org.example.springtraveljournal.models.dtos.request;

import org.example.springtraveljournal.models.enums.JournalVisibility;

import java.time.LocalDate;

public class JournalRequestUpdateDto {
    private String title;

    private String description;

    private JournalVisibility visibility;

    private LocalDate startDate;

    private LocalDate endDate;

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
