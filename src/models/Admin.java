package models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class Admin extends User {

    public Admin(String fullName) throws SQLException {
        super(fullName, UserRole.ADMIN);
    }

    public Admin(String fullName, UUID userID) throws SQLException {
        super(fullName, UserRole.ADMIN, userID);
    }
}
