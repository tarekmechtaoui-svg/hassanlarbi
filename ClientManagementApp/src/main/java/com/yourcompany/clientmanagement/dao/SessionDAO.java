package com.yourcompany.clientmanagement.dao;

import com.yourcompany.clientmanagement.model.Session;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    // Get all sessions
    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions ORDER BY year DESC, created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching sessions: " + e.getMessage());
            e.printStackTrace();
        }

        return sessions;
    }

    // Get active sessions only
    public List<Session> getActiveSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions WHERE is_active = TRUE ORDER BY year DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToSession(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching active sessions: " + e.getMessage());
            e.printStackTrace();
        }

        return sessions;
    }

    // Get current session
    public Session getCurrentSession() {
        String sql = "SELECT * FROM sessions WHERE is_current = TRUE LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return mapResultSetToSession(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching current session: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Get session by ID
    public Session getSessionById(int id) {
        String sql = "SELECT * FROM sessions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSession(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching session by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Insert new session
    public int insertSession(Session session) {
        String sql = "INSERT INTO sessions (year, name, start_date, end_date, is_active, is_current, description) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, session.getYear());
            stmt.setString(2, session.getName());
            stmt.setDate(3, Date.valueOf(session.getStartDate()));
            stmt.setDate(4, Date.valueOf(session.getEndDate()));
            stmt.setBoolean(5, session.isActive());
            stmt.setBoolean(6, session.isCurrent());
            stmt.setString(7, session.getDescription());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating session failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating session failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting session: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // Update session
    public boolean updateSession(Session session) {
        String sql = "UPDATE sessions SET year=?, name=?, start_date=?, end_date=?, is_active=?, is_current=?, description=?, updated_at=CURRENT_TIMESTAMP "
                + "WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, session.getYear());
            stmt.setString(2, session.getName());
            stmt.setDate(3, Date.valueOf(session.getStartDate()));
            stmt.setDate(4, Date.valueOf(session.getEndDate()));
            stmt.setBoolean(5, session.isActive());
            stmt.setBoolean(6, session.isCurrent());
            stmt.setString(7, session.getDescription());
            stmt.setInt(8, session.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating session: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Set current session (will automatically unset others via trigger)
    public boolean setCurrentSession(int sessionId) {
        String sql1 = "UPDATE sessions SET is_current = FALSE WHERE is_current = TRUE";
        String sql2 = "UPDATE sessions SET is_current = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(sql1);
             PreparedStatement stmt2 = conn.prepareStatement(sql2)) {

            // First unset all current sessions
            stmt1.executeUpdate();
            
            // Then set the new current session
            stmt2.setInt(1, sessionId);
            return stmt2.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error setting current session: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete session (only if no clients/versements are linked)
    public boolean deleteSession(int id) {
        // First check if session has any clients or versements
        if (hasLinkedData(id)) {
            System.err.println("Cannot delete session: it has linked clients or versements");
            return false;
        }

        String sql = "DELETE FROM sessions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting session: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Check if session has linked data
    private boolean hasLinkedData(int sessionId) {
        String clientSql = "SELECT COUNT(*) FROM clients WHERE session_id = ?";
        String versementSql = "SELECT COUNT(*) FROM versement WHERE session_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Check clients
            try (PreparedStatement stmt = conn.prepareStatement(clientSql)) {
                stmt.setInt(1, sessionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }

            // Check versements
            try (PreparedStatement stmt = conn.prepareStatement(versementSql)) {
                stmt.setInt(1, sessionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking linked data: " + e.getMessage());
            e.printStackTrace();
            return true; // Assume has data to be safe
        }

        return false;
    }

    // Create default session for a year
    public int createDefaultSessionForYear(String year) {
        Session session = new Session();
        session.setYear(year);
        session.setName("Session " + year);
        session.setStartDate(LocalDate.of(Integer.parseInt(year), 1, 1));
        session.setEndDate(LocalDate.of(Integer.parseInt(year), 12, 31));
        session.setActive(true);
        session.setCurrent(false);
        session.setDescription("Session par défaut pour l'année " + year);

        return insertSession(session);
    }

    // Map ResultSet to Session object
    private Session mapResultSetToSession(ResultSet rs) throws SQLException {
        Session session = new Session();
        session.setId(rs.getInt("id"));
        session.setYear(rs.getString("year"));
        session.setName(rs.getString("name"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            session.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            session.setEndDate(endDate.toLocalDate());
        }
        
        session.setActive(rs.getBoolean("is_active"));
        session.setCurrent(rs.getBoolean("is_current"));
        session.setDescription(rs.getString("description"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            session.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            session.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return session;
    }
}