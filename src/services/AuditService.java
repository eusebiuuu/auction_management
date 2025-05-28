package services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private static AuditService instance;
    private static final String FILE_NAME = "audit.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AuditService() {
        // Private constructor for singleton
    }

    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public void logAction(String actionName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            writer.println(actionName + "," + timestamp);
        } catch (IOException e) {
            System.err.println("Error writing to audit file: " + e.getMessage());
        }
    }
}
