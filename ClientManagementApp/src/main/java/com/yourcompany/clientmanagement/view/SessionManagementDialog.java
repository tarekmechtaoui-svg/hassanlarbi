package com.yourcompany.clientmanagement.view;

import com.yourcompany.clientmanagement.controller.SessionController;
import com.yourcompany.clientmanagement.model.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SessionManagementDialog extends JDialog {
    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private SessionController sessionController;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton switchButton, editButton, deleteButton;
    private boolean sessionChanged = false;

    public SessionManagementDialog(JFrame parent) {
        super(parent, "Gestion des Sessions", true);
        sessionController = new SessionController();
        initializeUI();
        setupTable();
        setupButtons();
        loadSessionData();
    }

    private void initializeUI() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        headerPanel.setBackground(new Color(248, 249, 250));

        JLabel titleLabel = new JLabel("📅 Gestion des Sessions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 152, 219));

        JLabel subtitleLabel = new JLabel("Gérez les différentes années/sessions de votre système");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(248, 249, 250));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void setupTable() {
        String[] columnNames = {
                "ID", "Année", "Nom", "Date Début", "Date Fin", 
                "Active", "Actuelle", "Description", "Créée le"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5 || columnIndex == 6) { // Active, Current columns
                    return Boolean.class;
                }
                return String.class;
            }
        };

        sessionTable = new JTable(tableModel);
        customizeTableAppearance();
        setupColumnWidths();

        // Hide ID column
        sessionTable.removeColumn(sessionTable.getColumnModel().getColumn(0));

        // Initialize sorter
        sorter = new TableRowSorter<>(tableModel);
        sessionTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(sessionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(scrollPane, BorderLayout.CENTER);

        // Add double-click listener to switch session
        sessionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    switchToSelectedSession();
                }
            }
        });

        // Add selection listener to enable/disable buttons
        sessionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
    }

    private void customizeTableAppearance() {
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 14);

        sessionTable.setFillsViewportHeight(true);
        sessionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        sessionTable.setFont(tableFont);
        sessionTable.setRowHeight(35);
        sessionTable.getTableHeader().setFont(headerFont);
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionTable.setShowGrid(true);
        sessionTable.setGridColor(new Color(220, 220, 220));
        sessionTable.setIntercellSpacing(new Dimension(1, 1));

        sessionTable.setSelectionBackground(new Color(52, 152, 219));
        sessionTable.setSelectionForeground(Color.WHITE);
    }

    private void setupColumnWidths() {
        // Column widths will be handled by AUTO_RESIZE_ALL_COLUMNS
        // But we can set preferred widths as hints
        sessionTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Année
        sessionTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Nom
        sessionTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Date Début
        sessionTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Date Fin
        sessionTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Active
        sessionTable.getColumnModel().getColumn(5).setPreferredWidth(70);  // Actuelle
        sessionTable.getColumnModel().getColumn(6).setPreferredWidth(200); // Description
        sessionTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Créée le
    }

    private void setupButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JButton addButton = createButton("➕ Nouvelle Session", e -> showAddDialog());
        editButton = createButton("✏️ Modifier", e -> showEditDialog());
        switchButton = createButton("🔄 Basculer vers", e -> switchToSelectedSession());
        deleteButton = createButton("🗑️ Supprimer", e -> deleteSession());
        JButton closeButton = createButton("Fermer", e -> dispose());

        // Style buttons
        addButton.setBackground(new Color(46, 125, 50));
        addButton.setForeground(Color.WHITE);
        
        switchButton.setBackground(new Color(52, 152, 219));
        switchButton.setForeground(Color.WHITE);
        
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(switchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initially disable buttons that require selection
        updateButtonStates();
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(140, 35));
        button.addActionListener(listener);
        button.setFocusPainted(false);
        return button;
    }

    private void updateButtonStates() {
        boolean hasSelection = sessionTable.getSelectedRow() != -1;
        editButton.setEnabled(hasSelection);
        switchButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);

        if (hasSelection) {
            int selectedRow = sessionTable.getSelectedRow();
            int modelRow = sessionTable.convertRowIndexToModel(selectedRow);
            boolean isCurrent = (Boolean) tableModel.getValueAt(modelRow, 6); // Current column
            switchButton.setEnabled(!isCurrent);
        }
    }

    private void loadSessionData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    List<Session> sessions = sessionController.fetchAllSessions();
                    SwingUtilities.invokeLater(() -> {
                        tableModel.setRowCount(0);
                        for (Session s : sessions) {
                            tableModel.addRow(convertSessionToRow(s));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(SessionManagementDialog.this,
                                "Erreur lors du chargement des sessions: " + e.getMessage(),
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }

            @Override
            protected void done() {
                sessionTable.repaint();
                updateButtonStates();
            }
        };
        worker.execute();
    }

    private Object[] convertSessionToRow(Session s) {
        return new Object[] {
                s.getId(),
                s.getYear(),
                s.getName(),
                s.getStartDate() != null ? s.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "",
                s.getEndDate() != null ? s.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "",
                s.isActive(),
                s.isCurrent(),
                s.getDescription(),
                s.getCreatedAt() != null ? s.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
        };
    }

    private void showAddDialog() {
        SessionDialog dialog = new SessionDialog(this, "Nouvelle Session", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Session newSession = dialog.getSession();
            int result = sessionController.addSession(newSession);
            if (result > 0) {
                loadSessionData();
                JOptionPane.showMessageDialog(this, "Session créée avec succès!");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow == -1) return;

        int modelRow = sessionTable.convertRowIndexToModel(selectedRow);
        // Always use the model's column index for ID, which is 0 in tableModel
        int sessionId = (Integer) tableModel.getValueAt(modelRow, 0);
        Session sessionToEdit = sessionController.getSessionById(sessionId);

        if (sessionToEdit != null) {
            SessionDialog dialog = new SessionDialog(this, "Modifier Session", sessionToEdit);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Session updatedSession = dialog.getSession();
                if (sessionController.updateSession(updatedSession)) {
                    loadSessionData();
                    JOptionPane.showMessageDialog(this, "Session modifiée avec succès!");
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void switchToSelectedSession() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow == -1) return;

        int modelRow = sessionTable.convertRowIndexToModel(selectedRow);
        int sessionId = (Integer) tableModel.getValueAt(modelRow, 0);
        String sessionName = (String) tableModel.getValueAt(modelRow, 2);
        boolean isCurrent = (Boolean) tableModel.getValueAt(modelRow, 6);

        if (isCurrent) {
            JOptionPane.showMessageDialog(this, "Cette session est déjà la session actuelle", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Basculer vers la session '" + sessionName + "'?\n\n" +
                "Cela changera la session active et vous verrez les données de cette session.",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (sessionController.setCurrentSession(sessionId)) {
                sessionChanged = true;
                loadSessionData();
                JOptionPane.showMessageDialog(this, 
                    "Session basculée avec succès vers '" + sessionName + "'!\n" +
                    "Les données affichées correspondent maintenant à cette session.");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors du changement de session", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSession() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une session", "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = sessionTable.convertRowIndexToModel(selectedRow);
        int sessionId = (Integer) tableModel.getValueAt(modelRow, 0);
        String sessionName = (String) tableModel.getValueAt(modelRow, 2);
        boolean isCurrent = (Boolean) tableModel.getValueAt(modelRow, 6);

        if (isCurrent) {
            JOptionPane.showMessageDialog(this, 
                "Impossible de supprimer la session actuelle.\n" +
                "Veuillez d'abord basculer vers une autre session.", 
                "Session actuelle", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer la session '" + sessionName + "'?\n\n" +
                "ATTENTION: Cette action supprimera également tous les clients\n" +
                "et versements associés à cette session.\n\n" +
                "Cette action est IRRÉVERSIBLE!",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Double confirmation for safety
            String confirmText = JOptionPane.showInputDialog(this,
                "Pour confirmer la suppression, tapez le nom de la session:\n'" + sessionName + "'",
                "Confirmation finale",
                JOptionPane.WARNING_MESSAGE);

            if (confirmText != null && confirmText.equals(sessionName)) {
                if (sessionController.deleteSession(sessionId)) {
                    loadSessionData();
                    JOptionPane.showMessageDialog(this, "Session supprimée avec succès!");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression.\n" +
                        "La session contient peut-être des données liées.", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else if (confirmText != null) {
                JOptionPane.showMessageDialog(this, 
                    "Le nom saisi ne correspond pas. Suppression annulée.", 
                    "Suppression annulée", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public boolean hasSessionChanged() {
        return sessionChanged;
    }
}