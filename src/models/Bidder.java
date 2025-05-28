package models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class Bidder extends User {
    public Bidder(String fullName) throws SQLException {
        super(fullName, UserRole.BIDDER);
    }

    public Bidder(String fullName, UUID userID) throws SQLException {
        super(fullName, UserRole.BIDDER, userID);
    }
}
