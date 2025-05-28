package models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class Initiator extends User {
    public Initiator(String fullName) throws SQLException {
        super(fullName, UserRole.INITIATOR);
    }

    public Initiator(String fullName, UUID userID) throws SQLException {
        super(fullName, UserRole.INITIATOR, userID);
    }
}
