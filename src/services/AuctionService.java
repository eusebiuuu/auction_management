package services;

import database.BidService;
import database.DatabaseConnection;
import database.GenericRepository;
import database.ItemService;
import models.Auction;
import models.Bid;
import models.Item;
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import java.util.*;

public class AuctionService extends GenericRepository<Auction> {
    private final HashMap<UUID, Auction> auctions;
    private final BidService bidManager;
    private final ItemService itemManager;
    private final UserService usersManager;

    public AuctionService() throws SQLException {
        super();
        this.auctions = new HashMap<>();
        bidManager = BidService.getInstance();
        itemManager = ItemService.getInstance();
        usersManager = UserService.getInstance();
        try {
            List<Auction> auctionsList = this.readAll();
            for (Auction a : auctionsList) {
                this.auctions.put(a.getAuctionID(), a);
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to read auctions: " + e.getMessage());
        }
    }

    private static AuctionService instance;

    public static synchronized AuctionService getInstance() throws SQLException {
        if (instance == null) {
            instance = new AuctionService();
        }
        return instance;
    }

    public void addAuction(Auction auction) {
        try {
            this.create(auction);
            System.out.println("Auction persisted to database: " + this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist auction to database: " + e.getMessage());
        }
        auctions.put(auction.getAuctionID(), auction);
    }

    public void deleteAuction(UUID auctionID) throws SQLException {
        Auction auction = getAuction(auctionID);
        for (Item item : auction.getItems()) {
            this.cancelBidding(auctionID, item.getItemID());
            for (Bid bid : item.getBids()) {
                bidManager.delete(bid.bidID());
            }
            itemManager.delete(item.getItemID());
        }
        this.delete(auctionID);
    }

    public void cancelBidding(UUID auctionID, UUID itemID) throws SQLException {
        Auction auction = this.getAuction(auctionID);
        Item item = auction.getItem(itemID);
        Bid lastBid = item.getLastBid();
        if (lastBid == null) {
            return;
        }
        User lastBidder = usersManager.getUser(lastBid.userID());
        double sum = lastBid.bidSum();
        UUID cardID = lastBid.cardID();
        Connection connection = DatabaseConnection.getInstance().getConnection();
        boolean finishBidding = false, updateBlockedSum = false;

        try {
            connection.setAutoCommit(false);
            item.finishBidding();
            finishBidding = true;
            lastBidder.updateBlockedSum(cardID, sum, -1);
            updateBlockedSum = true;
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            if (finishBidding) {
                item.rollbackBidding();
            }
            if (updateBlockedSum) {
                lastBidder.updateBlockedSumNoTransactions(cardID, sum, 1);
            }
            throw new SQLException(e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
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

    public void createAuction() throws SQLException {
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

    @Override
    protected String getTableName() {
        return "auctions";
    }

    @Override
    protected Auction mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        double fare = resultSet.getDouble("fare");
        String name = resultSet.getString("name");
        UUID auctionId = resultSet.getObject("auction_id", UUID.class);

        return new Auction(fare, name, auctionId);
    }

    @Override
    protected PreparedStatement createInsertStatement(Auction auction) throws SQLException {
        String sql = "INSERT INTO auctions (auction_id, name, fare) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, auction.getAuctionID());
        statement.setString(2, auction.getName());
        statement.setDouble(3, auction.getFare());
        return statement;
    }

    @Override
    protected PreparedStatement createUpdateStatement(Auction auction) throws SQLException {
        String sql = "UPDATE auctions SET name = ?, fare = ? WHERE auction_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, auction.getName());
        statement.setDouble(2, auction.getFare());
        statement.setObject(3, auction.getAuctionID());
        return statement;
    }

    @Override
    protected PreparedStatement createDeleteStatement(UUID auctionID) throws SQLException {
        String sql = "DELETE FROM auctions WHERE auction_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, auctionID);
        return statement;
    }
}