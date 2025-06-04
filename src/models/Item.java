package models;

import database.BidService;
import database.ItemService;

import java.sql.SQLException;
import java.util.*;

public class Item {
    private final UUID itemID;
    private String description;
    private final UUID userID;
    private final UUID cardID;
    private final UUID auctionID;
    private boolean active;
    private final Stack<Bid> bids;
    private final BidService bidService = BidService.getInstance();
    private final ItemService itemService = ItemService.getInstance();

    public Item(String description, UUID userID, UUID cardID, UUID auctionID) throws SQLException {
        itemID = UUID.randomUUID();
        this.description = description;
        this.userID = userID;
        this.auctionID = auctionID;
        this.cardID = cardID;
        this.active = true;
        this.bids = new Stack<>();
    }

    public Item(String description, UUID userID, UUID cardID, UUID auctionID, UUID itemID, boolean active) throws SQLException {
        this.itemID = itemID;
        this.description = description;
        this.userID = userID;
        this.auctionID = auctionID;
        this.cardID = cardID;
        this.active = active;

        this.bids = new Stack<>();
        List<Bid> bidsList = bidService.getBidsForItem(this.itemID);
        bidsList.sort(Comparator.comparing(Bid::bidSum));
        bids.addAll(bidsList);
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Bid getLastBid() {
        if (bids.isEmpty()) {
            return null;
        }
        return bids.peek();
    }

    public void addBid(Bid b) throws SQLException {
        try {
            bidService.create(b);
            bids.push(b);
        } catch (SQLException e) {
            throw new SQLException("Bid creation failed: " + e.getMessage());
        }
    }

    public void removeLastBid() {
        bids.pop();
    }

    public void setDescription(String description) throws SQLException {
        String oldDescription = this.description;
        try {
            this.description = description;
            itemService.update(this);
        } catch (Exception e) {
            this.description = oldDescription;
            throw new SQLException(e.getMessage());
        }
    }

    public void finishBidding() throws SQLException {
        try {
            this.active = false;
            itemService.update(this);
        } catch (Exception e) {
            this.active = true;
            throw new SQLException(e.getMessage());
        }
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

    public void rollbackBidding() {
        this.active = true;
    }

    public UUID getAuctionID() {
        return auctionID;
    }

    public UUID getCardID() {
        return cardID;
    }
}
