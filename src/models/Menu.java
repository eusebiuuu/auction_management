package models;

import services.AuctionService;
import services.UserService;

import javax.naming.AuthenticationException;
import java.util.*;

public final class Menu {
    private static volatile Menu instance;
    private final AuctionService auctionsManager;
    private final UserService usersManager;

    private Menu() {
        this.auctionsManager = new AuctionService();
        this.usersManager = new UserService();
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
            Scanner scan = new Scanner(System.in);
            System.out.println("Insert user type (0 - admin, 1 - bidder, 2 - initiator):");
            int type = scan.nextInt();
            if (type == 0) {
                createAdmin();
            } else if (type == 1) {
                createBidder();
            } else if (type == 2) {
                createInitiator();
            } else {
                throw new InputMismatchException("Invalid type provided");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void createBidder() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert bidder full name:");
        String username = scan.nextLine();
        usersManager.addUser(new Bidder(username));
    }

    public void createInitiator() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert initiator full name:");
        String username = scan.nextLine();
        usersManager.addUser(new Initiator(username));
    }

    public void createAdmin() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert admin full name:");
        String username = scan.nextLine();
        usersManager.addUser(new Admin(username));
    }

    public void deleteUser() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert user ID:");
        String strUserID = scan.nextLine();
        UUID userID = UUID.fromString(strUserID);
        if (!usersManager.removeUser(getUserID())) {
            throw new RuntimeException("No user with the given ID exists");
        } else {
            System.out.println("User deleted successfully");
        }
    }

    public void showAllUsers() {
        List<User> users = usersManager.getAllUsers();
        for (User user : users) {
            System.out.println(user.toString());
        }
    }

    private UUID getUserID() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert user ID:");
        return UUID.fromString(scan.nextLine());
    }

    public void showAllCards() {
        TreeSet<Card> cards = usersManager.getUser(getUserID()).getCards();
        for (Card c : cards) {
            System.out.println(c.toString());
        }
    }

    public void addCard() {
        User user = usersManager.getUser(getUserID());

        Scanner scan = new Scanner(System.in);
        System.out.println("Insert card info:");

        System.out.println("Holder name:");
        String holderName = scan.nextLine();

        System.out.println("Expiration month:");
        Integer month = scan.nextInt();

        System.out.println("Expiration year:");
        Integer year = scan.nextInt();

        user.addCard(new Card(holderName, month, year));
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
            double bidSum = scan.nextDouble();
            if (lastBidSum >= bidSum) {
                throw new InputMismatchException("Bid sum must be greater than " + lastBidSum);
            }

            System.out.println("Insert card ID:");
            UUID cardID = UUID.fromString(scan.nextLine());
            Card currentCard = currentUser.getCard(cardID);
            if (currentCard.getBalance() < bidSum) {
                throw new InputMismatchException("You don't have enough money to bid on this card");
            }

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

            Scanner scanner = new Scanner(System.in);

            System.out.println("Insert auction name:");
            String name = scanner.nextLine();

            System.out.println("Insert auction fare:");
            Double fare = scanner.nextDouble();

            auctionsManager.addAuction(new Auction(fare, name));
            System.out.println("Auction created successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showAllAuctions() {
        List<Auction> auctions = auctionsManager.getAuctions();
        for (Auction a : auctions) {
            System.out.println(a.toString());
        }
    }

    public void showAllItems() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert auction ID:");
        Auction auction = auctionsManager.getAuction(UUID.fromString(scanner.nextLine()));

        Collection<Item> items = auction.getItems();
        for (Item i : items) {
            System.out.println(i.toString());
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
        System.out.println("13 - Exit");
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}