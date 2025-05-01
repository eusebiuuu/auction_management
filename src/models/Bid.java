package models;

import java.util.UUID;

public record Bid(UUID bidID, UUID itemID, UUID userID, Double bidSum, UUID cardID) {
    public String toString() {
        return "Bid: " + bidID + ", " + bidSum + ", " + userID + ", " + itemID + ", " + cardID;
    }
}
