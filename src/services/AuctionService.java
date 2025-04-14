package services;

import models.Auction;

import java.util.*;

public class AuctionService {
    private final HashMap<UUID, Auction> auctions;

    public AuctionService() {
        this.auctions = new HashMap<>();
    }

    public void addAuction(Auction auction) {
        auctions.put(auction.getAuctionID(), auction);
    }

    public Auction getAuction(UUID auctionID) {
        if (auctions.get(auctionID) == null) {
            throw new NullPointerException("Invalid auction ID");
        }
        return auctions.get(auctionID);
    }

    public boolean removeAuction(UUID auctionId) {
        return auctions.remove(auctionId) != null;
    }

    public List<Auction> getAuctions() {
        return new ArrayList<>(auctions.values());
    }
}
