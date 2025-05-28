package models;

import database.ItemService;
import utils.Checker;

import java.sql.SQLException;
import java.util.*;

public class Auction {
    private final UUID auctionID;
    private final HashMap<UUID, Item> items;
    private final ItemService itemService = ItemService.getInstance();
    private final Double fare;
    private final String name;
    public Auction(Double fare, String name) {
        Checker.isPositive(fare);
        this.fare = fare;
        this.name = name;
        auctionID = UUID.randomUUID();
        items = new HashMap<>();
    }

    public Auction(Double fare, String name, UUID auctionID) throws SQLException {
        this.fare = fare;
        this.name = name;
        this.auctionID = auctionID;
        items = new HashMap<>();
        ItemService itemService = ItemService.getInstance();
        List<Item> itemsList = itemService.getActiveItems(auctionID);
        for (Item item : itemsList) {
            items.put(item.getItemID(), item);
        }
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

    public void removeItem(UUID itemID) {
        items.remove(itemID);
        System.out.println("Item removed successfully");
    }

    public void addItem(Item item) throws SQLException {
        try {
            itemService.create(item);
            items.put(item.getItemID(), item);
        } catch (Exception e) {
            throw new SQLException("Item creation didn't succeed: " + e.getMessage());
        }
    }

    public UUID getAuctionID() {
        return auctionID;
    }

    public Bid getLastBidSum(UUID itemID) {
        return items.get(itemID).getLastBid();
    }

    public void addBid(Bid b) throws SQLException {
        items.get(b.itemID()).addBid(b);
    }

    public void removeBid(UUID itemID) {
        items.get(itemID).removeLastBid();
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
