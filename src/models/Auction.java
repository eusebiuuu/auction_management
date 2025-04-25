package models;

import utils.Checker;

import java.util.*;

public class Auction {
    private final UUID auctionID;
    private final HashMap<UUID, Item> items;
    private Double fare;
    private final String name;
    public Auction(Double fare, String name) {
        Checker.isPositive(fare);
        this.fare = fare;
        this.name = name;
        auctionID = UUID.randomUUID();
        items = new HashMap<>();
    }

    public Item getItem(UUID itemID) {
        if (items.get(itemID) == null) {
            throw new NullPointerException("Invalid item ID");
        }
        return items.get(itemID);
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public void removeItem(UUID itemID) {
        items.remove(itemID);
        System.out.println("Item removed successfully");
    }

    public void addItem(Item item) {
        items.put(item.getItemID(), item);
        System.out.println("Item added successfully");
    }

    public UUID getAuctionID() {
        return auctionID;
    }

    public Bid getLastBidSum(UUID itemID) {
        return items.get(itemID).getLastBid();
    }
    public void addBid(Bid b) {
        items.get(b.itemID()).addBid(b);
    }

    public String getName() {
        return name;
    }

    public Collection<Item> getItems() {
        return items.values();
    }

    public String toString() {
        return "Auction: " + auctionID + ", " + name + ", " + fare;
    }
}
