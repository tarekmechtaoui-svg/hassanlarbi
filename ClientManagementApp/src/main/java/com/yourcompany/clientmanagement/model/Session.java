package com.yourcompany.clientmanagement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Session {
    private int id;
    private String year;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private boolean isCurrent;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Session() {}

    // Constructor with parameters
    public Session(String year, String name, LocalDate startDate, LocalDate endDate) {
        this.year = year;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = true;
        this.isCurrent = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getYear() {
        return year;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return name + " (" + year + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Session session = (Session) obj;
        return id == session.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}