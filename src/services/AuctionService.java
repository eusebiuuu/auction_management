package services;

import models.Auction;
import models.Item;

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

    public void showAllItems() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert auction ID:");
        Auction auction = getAuction(UUID.fromString(scanner.nextLine()));

        Collection<Item> items = auction.getItems();
        for (Item i : items) {
            System.out.println(i.toString());
        }
    }

    public void showAllAuctions() {
        List<Auction> auctions = getAuctions();
        for (Auction a : auctions) {
            System.out.println(a.toString());
        }
    }

    public void createAuction() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert auction name:");
        String name = scanner.nextLine();

        System.out.println("Insert auction fare:");
        Double fare = scanner.nextDouble();

        addAuction(new Auction(fare, name));
        System.out.println("Auction created successfully");
    }

    public List<Auction> getAuctions() {
        return new ArrayList<>(auctions.values());
    }
}
