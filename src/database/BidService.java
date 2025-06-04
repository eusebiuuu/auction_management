package database;

import database.GenericRepository;
import models.Auction;
import models.Bid;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BidService extends GenericRepository<Bid> {
    private static BidService instance;

    private BidService() {
        super();
    }

    public static synchronized BidService getInstance() {
        if (instance == null) {
            instance = new BidService();
        }
        return instance;
    }

    @Override
    protected String getTableName() {
        return "bids";
    }

    @Override
    protected Bid mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return new Bid(
                resultSet.getObject("bid_id", UUID.class),
                resultSet.getObject("item_id", UUID.class),
                resultSet.getObject("user_id", UUID.class),
                resultSet.getDouble("bid_sum"),
                resultSet.getObject("card_id", UUID.class)
        );
    }

    @Override
    protected PreparedStatement createInsertStatement(Bid bid) throws SQLException {
        String sql = "INSERT INTO bids (bid_id, item_id, user_id, bid_sum, card_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, bid.bidID());
        statement.setObject(2, bid.itemID());
        statement.setObject(3, bid.userID());
        statement.setDouble(4, bid.bidSum());
        statement.setObject(5, bid.cardID());
        return statement;
    }

    @Override
    protected PreparedStatement createUpdateStatement(Bid bid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Update operation in bids is not supported");
    }

    @Override
    protected PreparedStatement createDeleteStatement(UUID bidID) throws SQLException {
        String sql = "DELETE FROM bids WHERE bid_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, bidID);
        return statement;
    }

    public List<Bid> getBidsForItem(UUID itemId) throws SQLException {
        String sql = "SELECT * FROM bids WHERE item_id = ? ORDER BY bid_time DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, itemId);
            ResultSet resultSet = statement.executeQuery();
            List<Bid> bids = new ArrayList<>();
            while (resultSet.next()) {
                bids.add(mapResultSetToEntity(resultSet));
            }
            return bids;
        }
    }
}