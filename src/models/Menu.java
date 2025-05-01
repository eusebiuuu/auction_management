package models;

import services.AuctionService;
import services.UserService;

import javax.naming.AuthenticationException;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public final class Menu {
    private static volatile Menu instance;
    private final AuctionService auctionsManager;
    private final UserService usersManager;

    private Menu() {
        this.auctionsManager = new AuctionService();
        this.usersManager = new UserService();
        File f = new File("src/output.txt");

        try (FileWriter file = new FileWriter(f)) {
            Auction auction = new Auction(500.0, "oifjreiog");
            User user = new Admin("Bob Smith");
            Card card = new Card("fjrifj", 4, 2026);
            Item item = new Item("forjeifjoreh", user.getUserID());
            card.setBalance(300.0);
            user.addCard(card);
            auction.addItem(item);
            auctionsManager.addAuction(auction);
            usersManager.addUser(user);

            file.write("Auction ID: " + auction.getAuctionID() + "\n");
            file.write("Card ID: " + card.getCode() + "\n");
            file.write("User ID: " + user.getUserID() + "\n");
            file.write("Item ID: " + item.getItemID() + "\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Menu getInstance() {
        if (instance == null) {
            synchronized (Menu.class) {
                if (instance == null) {
                    instance = new Menu();
                }
            }
        }
        return instance;
    }

    public void createUser() {
        try {
            usersManager.createUser();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteUser() {
        try {
            usersManager.removeUser(getUserID());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private UUID getUserID() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert user ID:");
        return UUID.fromString(scan.nextLine());
    }

    public void showAllCards() {
        try {
            usersManager.showAllCard(getUserID());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addCard() {
        try {
            usersManager.createCard(getUserID());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showAllUsers() {
        try {
            usersManager.showAllUsers(getUserID());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void makeBid() {
        try {
            UUID userID = getUserID();
            User currentUser = usersManager.getUser(userID);

            if (!(currentUser instanceof Initiator) && !(currentUser instanceof Admin)) {
                throw new AuthenticationException("You are not allowed to perform this action");
            }

            Scanner scan = new Scanner(System.in);

            System.out.println("Insert auction ID:");
            Auction auction = auctionsManager.getAuction(UUID.fromString(scan.nextLine()));

            System.out.println("Insert item ID:");
            Item item = auction.getItem(UUID.fromString(scan.nextLine()));
            if (!item.isActive()) {
                throw new RuntimeException("The item is not available for bidding");
            }

            Bid lastBid = auction.getLastBidSum(item.getItemID());
            double lastBidSum = lastBid == null ? 0.0 : lastBid.bidSum();
            System.out.println("Insert sum to bid (last bid was " + lastBidSum + "):");
            double bidSum = Double.parseDouble(scan.nextLine());
            if (lastBidSum >= bidSum) {
                throw new InputMismatchException("Bid sum must be greater than " + lastBidSum);
            }

            System.out.println("Insert card ID:");
            UUID cardID = UUID.fromString(scan.nextLine());
            currentUser.getCard(cardID);

            if (lastBid != null) {
                User lastBidder = usersManager.getUser(lastBid.userID());
                lastBidder.updateBlockedSum(lastBid.cardID(), lastBid.bidSum(), -1);
            }

            currentUser.updateBlockedSum(cardID, bidSum, 1);
            auction.addBid(new Bid(UUID.randomUUID(), item.getItemID(), userID, bidSum, cardID));

            System.out.println("Bid processed successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void createAuction() {
        try {
            UUID userID = getUserID();
            User currentUser = usersManager.getUser(userID);

            if (!(currentUser instanceof Admin)) {
                throw new AuthenticationException("You are not allowed to perform this action");
            }

            auctionsManager.createAuction();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showAllAuctions() {
        try {
            auctionsManager.showAllAuctions();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showAllItems() {
        try {
            auctionsManager.showAllItems();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addItem() {
        try {
            User user = usersManager.getUser(getUserID());
            if (!(user instanceof Initiator) && !(user instanceof Admin)) {
                throw new AuthenticationException("Your are not allowed to perform this action");
            }
            Scanner scanner = new Scanner(System.in);

            System.out.println("Insert auction ID:");
            Auction auction = auctionsManager.getAuction(UUID.fromString(scanner.nextLine()));

            System.out.println("Insert description:");
            String description = scanner.nextLine();

            System.out.println("Insert card:");
            Card card = user.getCard(UUID.fromString(scanner.nextLine()));
            card.setBalance(card.getBalance() - auction.getFare());

            auction.addItem(new Item(description, user.getUserID()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void finishBidding() {
        try {
            User user = usersManager.getUser(getUserID());
            if (!(user instanceof Initiator) && !(user instanceof Admin)) {
                throw new AuthenticationException("Your are not allowed to perform this action");
            }
            Scanner scanner = new Scanner(System.in);

            System.out.println("Insert auction ID:");
            Auction auction = auctionsManager.getAuction(UUID.fromString(scanner.nextLine()));

            System.out.println("Insert item ID:");
            Item item = auction.getItem(UUID.fromString(scanner.nextLine()));
            item.finishBidding();

            Bid lastBid = item.getLastBid();
            if (lastBid == null) {
                System.out.println("Empty bidding");
                return;
            }

            User lastUser = usersManager.getUser(lastBid.userID());
            lastUser.updateBlockedSum(lastBid.cardID(), lastBid.bidSum(), -1);
            Card card = user.getCard(lastBid.cardID());
            card.setBalance(card.getBalance() - lastBid.bidSum());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addSumToCard() {
        try {
            Scanner scan = new Scanner(System.in);

            User user = usersManager.getUser(getUserID());
            System.out.println("Insert card ID:");
            Card card = user.getCard(UUID.fromString(scan.nextLine()));

            System.out.println("Insert sum to add:");
            double sum = scan.nextDouble();
            card.setBalance(card.getBalance() + sum);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showAllProductBids() {
        try {
            Scanner scan = new Scanner(System.in);
            User user = usersManager.getUser(getUserID());

            if (!(user instanceof Admin) && !(user instanceof Initiator)) {
                throw new AuthenticationException("You cannot perform this action");
            }
            System.out.println("Insert auction ID:");
            UUID auctionID = UUID.fromString(scan.nextLine());

            System.out.println("Insert item ID:");
            UUID itemID = UUID.fromString(scan.nextLine());

            Auction auction = auctionsManager.getAuction(auctionID);
            Item item = auction.getItem(itemID);

//            if (!item.isActive()) {
//                throw new RuntimeException("The auction has been finished");
//            }

            if (!item.getUserID().equals(user.getUserID())) {
                throw new AuthenticationException("You cannot access this information");
            }

            Stack<Bid> bids = item.getBids();
            for (Bid b : bids) {
                System.out.println(b);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showAllMadeBids() {
        try {
            User user = usersManager.getUser(getUserID());

            if (!(user instanceof Admin) && !(user instanceof Bidder)) {
                throw new AuthenticationException("You cannot perform this action");
            }

            TreeSet<Bid> bids = new TreeSet<>(Comparator.comparing(Bid::bidSum));
            Collection<Auction> auctions = auctionsManager.getAuctions();
            for (Auction a : auctions) {
                Collection<Item> items = a.getItems();
                System.out.println(a);
                for (Item item : items) {
                    System.out.println(item);
                    if (item.isActive() && item.getLastBid().userID().equals(user.getUserID())) {
                        bids.add(item.getLastBid());
                    }
                }
            }
            System.out.println(bids.size());

            System.out.println("Relevant bids in increasing number of bid sum:");
            for (Bid b : bids) {
                System.out.println(b);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void cancelBidding() {
        try {
            Scanner scanner = new Scanner(System.in);
            User user = usersManager.getUser(getUserID());

            if (!(user instanceof Admin)) {
                throw new AuthenticationException("You cannot perform this action");
            }

            System.out.println("Insert auction ID:");
            UUID auctionID = UUID.fromString(scanner.nextLine());

            System.out.println("Insert item ID:");
            UUID itemID = UUID.fromString(scanner.nextLine());

            Auction auction = auctionsManager.getAuction(auctionID);
            Item item = auction.getItem(itemID);
            item.finishBidding();

            Bid lastBid = item.getLastBid();
            User lastBidder = usersManager.getUser(lastBid.userID());
            double sum = lastBid.bidSum();
            UUID cardID = lastBid.cardID();
            lastBidder.updateBlockedSum(cardID, sum, -1);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void printMenu() {
        System.out.println("1 - Create user");
        System.out.println("2 - Delete user");
        System.out.println("3 - Show all users");
        System.out.println("4 - Show all cards");
        System.out.println("5 - Add card");
        System.out.println("6 - Create auction");
        System.out.println("7 - Show all auctions");
        System.out.println("8 - Show all items");
        System.out.println("9 - Create item");
        System.out.println("10 - Finish auction");
        System.out.println("11 - Make bid");
        System.out.println("12 - Add sum to card");
        System.out.println("13 - Show all product bids");
        System.out.println("14 - Show all personal bids");
        System.out.println("15 - Cancel bidding");
        System.out.println("16 - Exit");
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}