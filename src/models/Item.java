package models;

import java.util.Objects;
import java.util.Stack;
import java.util.UUID;

public class Item {
    private final UUID itemID;
    private String description;
    private final UUID userID;
    private Boolean active;
    private final Stack<Bid> bids;

    public Item(String description, UUID userID) {
        itemID = UUID.randomUUID();
        this.description = description;
        this.userID = userID;
        this.bids = new Stack<>();
        active = true;
    }

    public String getDescription() {
        return description;
    }

    public Boolean isActive() {
        return active;
    }

    public Bid getLastBid() {
        if (bids.isEmpty()) {
            return null;
        }
        return bids.peek();
    }

    public void addBid(Bid b) {
        bids.push(b);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void finishBidding() {
        this.active = false;
    }

    public UUID getItemID() {
        return itemID;
    }

    public String toString() {
        return "Item: " + itemID + ", " + description + ", " + userID;
    }

    public Stack<Bid> getBids() {
        return bids;
    }

    public UUID getUserID() {
        return userID;
    }
}
