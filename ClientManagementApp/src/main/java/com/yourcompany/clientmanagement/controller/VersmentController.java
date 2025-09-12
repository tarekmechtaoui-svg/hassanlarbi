package com.yourcompany.clientmanagement.controller;

import com.yourcompany.clientmanagement.dao.VersmentDAO;
import com.yourcompany.clientmanagement.dao.ClientDAO;
import com.yourcompany.clientmanagement.model.Versment;
import com.yourcompany.clientmanagement.model.Client;
import com.yourcompany.clientmanagement.controller.SessionController;
import com.yourcompany.clientmanagement.model.Session;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class VersmentController {
    private VersmentDAO versmentDAO;
    private ClientDAO clientDAO;
    private SessionController sessionController;

    public VersmentController() {
        versmentDAO = new VersmentDAO();
        clientDAO = new ClientDAO();
        sessionController = new SessionController();
    }

    // 🔄 1. Fetch all versments
    public List<Versment> fetchAllVersments() {
        Session currentSession = sessionController.getCurrentSession();
        if (currentSession != null) {
            return versmentDAO.getAllVersments(currentSession.getId());
        }
        return new ArrayList<>();
    }

    // 🔄 1b. Fetch all versments for specific session
    public List<Versment> fetchAllVersments(int sessionId) {
        return versmentDAO.getAllVersments(sessionId);
    }

    // 🔄 1c. Fetch all versments from all sessions (admin function)
    public List<Versment> fetchAllVersmentsAllSessions() {
        return versmentDAO.getAllVersmentsAllSessions();
    }

    // 🔄 2. Fetch versments by client ID
    public List<Versment> fetchVersmentsByClientId(int clientId) {
        return versmentDAO.getVersmentsByClientId(clientId);
    }

    // ➕ 3. Add a versment
    public int addVersment(Versment versment) {
        if (versment == null) {
            throw new IllegalArgumentException("Versment cannot be null");
        }
        if (versment.getClientId() <= 0) {
            throw new IllegalArgumentException("Valid client ID is required");
        }
        if (versment.getMontant() == null || versment.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid amount is required");
        }
        
        // Set session if not already set
        if (versment.getSessionId() <= 0) {
            // Get client's session
            Client client = clientDAO.getClientById(versment.getClientId());
            if (client != null) {
                versment.setSessionId(client.getSessionId());
            } else {
                throw new IllegalArgumentException("Client not found");
            }
        }

        // Insert the versment - database triggers will handle balance update automatically
        int versmentId = versmentDAO.insertVersment(versment);
        return versmentId;
    }

    // ✏️ 4. Update a versment
    public boolean updateVersment(Versment versment) {
        if (versment == null) {
            throw new IllegalArgumentException("Versment cannot be null");
        }
        if (versment.getId() <= 0) {
            throw new IllegalArgumentException("Valid versment ID is required for update");
        }
        
        // Update the versment - database triggers will handle balance update automatically
        return versmentDAO.updateVersment(versment);
    }

    // ❌ 5. Delete a versment by ID
    public boolean deleteVersment(int versmentId) {
        if (versmentId <= 0) {
            throw new IllegalArgumentException("Valid versment ID is required");
        }
        
        // Delete the versment - database triggers will handle balance update automatically
        return versmentDAO.deleteVersmentById(versmentId);
    }

    // 🔎 6. Get versment by ID
    public Versment getVersmentById(int id) {
        return versmentDAO.getVersmentById(id);
    }

    // 💰 7. Get total versments amount by client ID
    public BigDecimal getTotalVersmentsByClientId(int clientId) {
        return versmentDAO.getTotalVersmentsByClientId(clientId);
    }

    // 💰 8. Get remaining amount for a client (now from database)
    public BigDecimal getRemainingAmountForClient(int clientId) {
        return clientDAO.getRemainingBalanceById(clientId);
    }

    // 🔄 9. Manually recalculate balance for a client (if needed)
    public boolean recalculateClientBalance(int clientId) {
        return clientDAO.recalculateClientBalance(clientId);
    }

    // 🔄 10. Manually recalculate all balances (if needed)
    public boolean recalculateAllBalances() {
        return clientDAO.recalculateAllBalances();
    }
}