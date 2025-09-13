package com.yourcompany.clientmanagement.view;

import com.yourcompany.clientmanagement.model.Session;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class SessionDialog extends JDialog {
    private JTextField yearField, nameField, descriptionField;
    private JXDatePicker startDatePicker, endDatePicker;
    private JCheckBox activeCheckBox, currentCheckBox;
    private boolean confirmed = false;
    private Session session;

    public SessionDialog(JDialog parent, String title, Session session) {
        super(parent, title, true);
        this.session = session;
        initializeUI();
        populateFields();
    }

    private void initializeUI() {
        setSize(500, 450);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        headerPanel.setBackground(new Color(248, 249, 250));

        JLabel titleLabel = new JLabel("📅 " + getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 152, 219));

        JLabel subtitleLabel = new JLabel("Configurez les paramètres de la session");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(108, 117, 125));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(248, 249, 250));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Year field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel yearLabel = new JLabel("Année*:");
        yearLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(yearLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        yearField = createStyledTextField("Ex: 2024");
        addYearValidation(yearField);
        formPanel.add(yearField, gbc);

        // Name field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel nameLabel = new JLabel("Nom de la session*:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = createStyledTextField("Ex: Session 2024");
        formPanel.add(nameField, gbc);

        // Start date
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel startDateLabel = new JLabel("Date de début*:");
        startDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(startDateLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        startDatePicker = new JXDatePicker();
        startDatePicker.setFormats("dd/MM/yyyy");
        startDatePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleComponent(startDatePicker);
        formPanel.add(startDatePicker, gbc);

        // End date
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel endDateLabel = new JLabel("Date de fin*:");
        endDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(endDateLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        endDatePicker = new JXDatePicker();
        endDatePicker.setFormats("dd/MM/yyyy");
        endDatePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleComponent(endDatePicker);
        formPanel.add(endDatePicker, gbc);

        // Description field
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        descriptionField = createStyledTextField("Description optionnelle");
        formPanel.add(descriptionField, gbc);

        // Checkboxes
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.setBackground(Color.WHITE);

        activeCheckBox = new JCheckBox("Session active");
        activeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        activeCheckBox.setSelected(true);
        activeCheckBox.setBackground(Color.WHITE);

        currentCheckBox = new JCheckBox("Définir comme session actuelle");
        currentCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentCheckBox.setBackground(Color.WHITE);

        checkboxPanel.add(activeCheckBox);
        checkboxPanel.add(Box.createHorizontalStrut(20));
        checkboxPanel.add(currentCheckBox);
        formPanel.add(checkboxPanel, gbc);

        // Info panel
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        infoPanel.setBackground(new Color(255, 248, 225));

        JLabel infoIcon = new JLabel("💡");
        infoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel infoText = new JLabel("<html><b>Information:</b><br>" +
                "• Une seule session peut être 'actuelle' à la fois<br>" +
                "• La session actuelle détermine quelles données sont affichées<br>" +
                "• Les sessions inactives ne peuvent pas être définies comme actuelles</html>");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        infoPanel.add(infoIcon, BorderLayout.WEST);
        infoPanel.add(infoText, BorderLayout.CENTER);
        formPanel.add(infoPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 10));

        JButton okButton = new JButton("💾 Enregistrer");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setPreferredSize(new Dimension(140, 40));
        okButton.setBackground(new Color(46, 125, 50));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> validateAndClose());

        JButton cancelButton = new JButton("❌ Annuler");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(140, 40));
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);

        // Add listeners for auto-generation
        yearField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                autoGenerateSessionName();
            }
        });
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    private void styleComponent(JComponent component) {
        component.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void addYearValidation(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
                // Limit to 4 digits
                if (field.getText().length() >= 4 && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
    }

    private void autoGenerateSessionName() {
        String year = yearField.getText().trim();
        if (!year.isEmpty() && nameField.getText().trim().isEmpty()) {
            nameField.setText("Session " + year);
        }
    }

    private void populateFields() {
        if (session == null) {
            // Set defaults for new session
            String currentYear = String.valueOf(LocalDate.now().getYear());
            yearField.setText(currentYear);
            nameField.setText("Session " + currentYear);
            startDatePicker.setDate(Date.from(LocalDate.of(Integer.parseInt(currentYear), 1, 1)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            endDatePicker.setDate(Date.from(LocalDate.of(Integer.parseInt(currentYear), 12, 31)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
            return;
        }

        yearField.setText(session.getYear());
        nameField.setText(session.getName());
        
        if (session.getStartDate() != null) {
            startDatePicker.setDate(Date.from(session.getStartDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        if (session.getEndDate() != null) {
            endDatePicker.setDate(Date.from(session.getEndDate()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        
        if (session.getDescription() != null) {
            descriptionField.setText(session.getDescription());
        }
        
        activeCheckBox.setSelected(session.isActive());
        currentCheckBox.setSelected(session.isCurrent());
    }

    private void validateAndClose() {
        // Validate required fields
        if (yearField.getText().trim().isEmpty()) {
            showValidationError("L'année est obligatoire", yearField);
            return;
        }

        if (nameField.getText().trim().isEmpty()) {
            showValidationError("Le nom de la session est obligatoire", nameField);
            return;
        }

        if (startDatePicker.getDate() == null) {
            showValidationError("La date de début est obligatoire", startDatePicker);
            return;
        }

        if (endDatePicker.getDate() == null) {
            showValidationError("La date de fin est obligatoire", endDatePicker);
            return;
        }

        // Validate year format
        String year = yearField.getText().trim();
        try {
            int yearInt = Integer.parseInt(year);
            if (yearInt < 1900 || yearInt > 2100) {
                showValidationError("L'année doit être entre 1900 et 2100", yearField);
                return;
            }
        } catch (NumberFormatException e) {
            showValidationError("Format d'année invalide", yearField);
            return;
        }

        // Validate date range
        Date startDate = startDatePicker.getDate();
        Date endDate = endDatePicker.getDate();
        
        if (startDate.after(endDate)) {
            showValidationError("La date de début doit être antérieure à la date de fin", startDatePicker);
            return;
        }

        // Validate that current checkbox is only selected if active is selected
        if (currentCheckBox.isSelected() && !activeCheckBox.isSelected()) {
            showValidationError("Une session doit être active pour être définie comme actuelle", currentCheckBox);
            return;
        }

        confirmed = true;
        dispose();
    }

    private void showValidationError(String message, JComponent field) {
        JOptionPane.showMessageDialog(this, message, "Validation", JOptionPane.WARNING_MESSAGE);
        field.requestFocus();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Session getSession() {
        if (!confirmed) return null;

        Session s = session != null ? session : new Session();
        
        s.setYear(yearField.getText().trim());
        s.setName(nameField.getText().trim());
        
        // Convert dates
        Date startDate = startDatePicker.getDate();
        if (startDate != null) {
            s.setStartDate(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        Date endDate = endDatePicker.getDate();
        if (endDate != null) {
            s.setEndDate(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        
        s.setDescription(descriptionField.getText().trim());
        s.setActive(activeCheckBox.isSelected());
        s.setCurrent(currentCheckBox.isSelected());

        return s;
    }
}