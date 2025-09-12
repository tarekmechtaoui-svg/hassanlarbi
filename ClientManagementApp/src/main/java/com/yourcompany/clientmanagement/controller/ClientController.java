package com.yourcompany.clientmanagement.controller;

import com.yourcompany.clientmanagement.dao.ClientDAO;
import com.yourcompany.clientmanagement.model.Client;
import com.yourcompany.clientmanagement.controller.SessionController;
import com.yourcompany.clientmanagement.model.Session;

import java.util.List;
import java.util.ArrayList;

public class ClientController {
    private ClientDAO clientDAO;
    private SessionController sessionController;

    public ClientController() {
        clientDAO = new ClientDAO();
        sessionController = new SessionController();
    }

    // 🔄 1. Fetch all clients
    public List<Client> fetchAllClients() {
        Session currentSession = sessionController.getCurrentSession();
        if (currentSession != null) {
            return clientDAO.getAllClients(currentSession.getId());
        }
        return new ArrayList<>();
    }

    // 🔄 1b. Fetch all clients for specific session
    public List<Client> fetchAllClients(int sessionId) {
        return clientDAO.getAllClients(sessionId);
    }

    // 🔄 1c. Fetch all clients from all sessions (admin function)
    public List<Client> fetchAllClientsAllSessions() {
        return clientDAO.getAllClientsAllSessions();
    }

    // ➕ 2. Add a client
    public int addClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Client name is required");
        }
        if (client.getActivite() == null || client.getActivite().trim().isEmpty()) {
            throw new IllegalArgumentException("Client activity is required");
        }
        
        // Set session if not already set
        if (client.getSessionId() <= 0) {
            Session currentSession = sessionController.getCurrentSession();
            if (currentSession != null) {
                client.setSessionId(currentSession.getId());
            } else {
                throw new IllegalStateException("No current session available");
            }
        }
        
        return clientDAO.insertClient(client);
    }

    // ✏️ 3. Update a client
    public boolean updateClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (client.getId() <= 0) {
            throw new IllegalArgumentException("Valid client ID is required for update");
        }
        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Client name is required");
        }
        if (client.getActivite() == null || client.getActivite().trim().isEmpty()) {
            throw new IllegalArgumentException("Client activity is required");
        }
        return clientDAO.updateClient(client);
    }

    // ❌ 4. Delete a client by ID
    public boolean deleteClient(int clientId) {
        if (clientId <= 0) {
            throw new IllegalArgumentException("Valid client ID is required");
        }
        return clientDAO.deleteClientById(clientId);
    }

    // 🔎 5. Optional: Search by name or other field
    public List<Client> searchClients(String keyword) {
        if (keyword == null) {
            keyword = "";
        }
        Session currentSession = sessionController.getCurrentSession();
        if (currentSession != null) {
            return clientDAO.searchClients(keyword, currentSession.getId());
        }
        return new ArrayList<>();
    }

    // 🔎 5b. Search clients in specific session
    public List<Client> searchClients(String keyword, int sessionId) {
        if (keyword == null) {
            keyword = "";
        }
        return clientDAO.searchClients(keyword, sessionId);
    }
    
    public Client getClientById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Valid client ID is required");
        }
        return clientDAO.getClientById(id);
    }
}
