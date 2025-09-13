-- Create database if not exists
CREATE DATABASE IF NOT EXISTS client_management;
USE client_management;

-- Create sessions table
CREATE TABLE IF NOT EXISTS sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    year VARCHAR(10) NOT NULL,
    name VARCHAR(200) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_current BOOLEAN DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_year (year),
    UNIQUE KEY unique_current (is_current, id)
);

-- Update clients table to include session_id and remaining_balance
ALTER TABLE clients 
ADD COLUMN IF NOT EXISTS session_id INT DEFAULT 1,
ADD COLUMN IF NOT EXISTS remaining_balance DECIMAL(15,2) DEFAULT 0.00,
ADD FOREIGN KEY IF NOT EXISTS (session_id) REFERENCES sessions(id) ON DELETE CASCADE;

-- Update versement table to include session_id
ALTER TABLE versement 
ADD COLUMN IF NOT EXISTS session_id INT DEFAULT 1,
ADD FOREIGN KEY IF NOT EXISTS (session_id) REFERENCES sessions(id) ON DELETE CASCADE;

-- Create default session for current year if none exists
INSERT IGNORE INTO sessions (year, name, start_date, end_date, is_active, is_current, description)
VALUES ('2025', 'Session 2025', '2025-01-01', '2025-12-31', TRUE, TRUE, 'Session par défaut pour l\'année 2025');

-- Create triggers for automatic balance calculation
DELIMITER //

-- Trigger to update client balance when versement is inserted
CREATE TRIGGER IF NOT EXISTS update_balance_after_versement_insert
AFTER INSERT ON versement
FOR EACH ROW
BEGIN
    UPDATE clients 
    SET remaining_balance = COALESCE(montant, 0) - (
        SELECT COALESCE(SUM(montant), 0) 
        FROM versement 
        WHERE client_id = NEW.client_id AND session_id = NEW.session_id
    )
    WHERE id = NEW.client_id;
END//

-- Trigger to update client balance when versement is updated
CREATE TRIGGER IF NOT EXISTS update_balance_after_versement_update
AFTER UPDATE ON versement
FOR EACH ROW
BEGIN
    UPDATE clients 
    SET remaining_balance = COALESCE(montant, 0) - (
        SELECT COALESCE(SUM(montant), 0) 
        FROM versement 
        WHERE client_id = NEW.client_id AND session_id = NEW.session_id
    )
    WHERE id = NEW.client_id;
END//

-- Trigger to update client balance when versement is deleted
CREATE TRIGGER IF NOT EXISTS update_balance_after_versement_delete
AFTER DELETE ON versement
FOR EACH ROW
BEGIN
    UPDATE clients 
    SET remaining_balance = COALESCE(montant, 0) - (
        SELECT COALESCE(SUM(montant), 0) 
        FROM versement 
        WHERE client_id = OLD.client_id AND session_id = OLD.session_id
    )
    WHERE id = OLD.client_id;
END//

-- Trigger to set remaining_balance when client is inserted
CREATE TRIGGER IF NOT EXISTS set_initial_balance_after_client_insert
AFTER INSERT ON clients
FOR EACH ROW
BEGIN
    UPDATE clients 
    SET remaining_balance = COALESCE(montant, 0)
    WHERE id = NEW.id;
END//

-- Trigger to update remaining_balance when client montant is updated
CREATE TRIGGER IF NOT EXISTS update_balance_after_client_update
AFTER UPDATE ON clients
FOR EACH ROW
BEGIN
    IF NEW.montant != OLD.montant THEN
        UPDATE clients 
        SET remaining_balance = COALESCE(NEW.montant, 0) - (
            SELECT COALESCE(SUM(montant), 0) 
            FROM versement 
            WHERE client_id = NEW.id AND session_id = NEW.session_id
        )
        WHERE id = NEW.id;
    END IF;
END//

DELIMITER ;

-- Stored procedures for balance recalculation
DELIMITER //

CREATE PROCEDURE IF NOT EXISTS RecalculateClientBalance(IN client_id INT)
BEGIN
    UPDATE clients c
    SET remaining_balance = COALESCE(c.montant, 0) - (
        SELECT COALESCE(SUM(v.montant), 0)
        FROM versement v
        WHERE v.client_id = client_id AND v.session_id = c.session_id
    )
    WHERE c.id = client_id;
END//

CREATE PROCEDURE IF NOT EXISTS RecalculateAllBalances()
BEGIN
    UPDATE clients c
    SET remaining_balance = COALESCE(c.montant, 0) - (
        SELECT COALESCE(SUM(v.montant), 0)
        FROM versement v
        WHERE v.client_id = c.id AND v.session_id = c.session_id
    );
END//

DELIMITER ;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_clients_session_id ON clients(session_id);
CREATE INDEX IF NOT EXISTS idx_versement_session_id ON versement(session_id);
CREATE INDEX IF NOT EXISTS idx_sessions_year ON sessions(year);
CREATE INDEX IF NOT EXISTS idx_sessions_current ON sessions(is_current);