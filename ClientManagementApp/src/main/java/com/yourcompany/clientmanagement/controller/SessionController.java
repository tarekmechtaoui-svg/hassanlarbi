package com.yourcompany.clientmanagement.controller;

import com.yourcompany.clientmanagement.dao.SessionDAO;
import com.yourcompany.clientmanagement.model.Session;

import java.time.LocalDate;
import java.util.List;

public class SessionController {
    private SessionDAO sessionDAO;

    public SessionController() {
        sessionDAO = new SessionDAO();
    }

    // Get all sessions
    public List<Session> fetchAllSessions() {
        return sessionDAO.getAllSessions();
    }

    // Get active sessions only
    public List<Session> fetchActiveSessions() {
        return sessionDAO.getActiveSessions();
    }

    // Get current session
    public Session getCurrentSession() {
        Session current = sessionDAO.getCurrentSession();
        
        // If no current session exists, create one for current year
        if (current == null) {
            String currentYear = String.valueOf(LocalDate.now().getYear());
            int sessionId = sessionDAO.createDefaultSessionForYear(currentYear);
            if (sessionId > 0) {
                sessionDAO.setCurrentSession(sessionId);
                current = sessionDAO.getSessionById(sessionId);
            }
        }
        
        return current;
    }

    // Add a new session
    public int addSession(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        if (session.getYear() == null || session.getYear().trim().isEmpty()) {
            throw new IllegalArgumentException("Session year is required");
        }
        if (session.getName() == null || session.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Session name is required");
        }
        if (session.getStartDate() == null) {
            throw new IllegalArgumentException("Session start date is required");
        }
        if (session.getEndDate() == null) {
            throw new IllegalArgumentException("Session end date is required");
        }
        if (session.getStartDate().isAfter(session.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        return sessionDAO.insertSession(session);
    }

    // Update a session
    public boolean updateSession(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        if (session.getId() <= 0) {
            throw new IllegalArgumentException("Valid session ID is required for update");
        }

        return sessionDAO.updateSession(session);
    }

    // Delete a session
    public boolean deleteSession(int sessionId) {
        if (sessionId <= 0) {
            throw new IllegalArgumentException("Valid session ID is required");
        }

        return sessionDAO.deleteSession(sessionId);
    }

    // Set current session
    public boolean setCurrentSession(int sessionId) {
        if (sessionId <= 0) {
            throw new IllegalArgumentException("Valid session ID is required");
        }

        return sessionDAO.setCurrentSession(sessionId);
    }

    // Get session by ID
    public Session getSessionById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Valid session ID is required");
        }

        return sessionDAO.getSessionById(id);
    }

    // Create session for specific year
    public int createSessionForYear(String year) {
        if (year == null || year.trim().isEmpty()) {
            throw new IllegalArgumentException("Year is required");
        }

        try {
            Integer.parseInt(year); // Validate year format
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid year format");
        }

        return sessionDAO.createDefaultSessionForYear(year);
    }

    // Switch to a different session
    public boolean switchToSession(int sessionId) {
        Session session = sessionDAO.getSessionById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        if (!session.isActive()) {
            throw new IllegalArgumentException("Cannot switch to inactive session");
        }

        return sessionDAO.setCurrentSession(sessionId);
    }
}