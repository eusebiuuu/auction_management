package database;

import models.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemService extends GenericRepository<Item> {
    private static ItemService instance;

    private ItemService() {
        super();
    }

    public static synchronized ItemService getInstance() {
        if (instance == null) {
            instance = new ItemService();
        }
        return instance;
    }

    @Override
    protected String getTableName() {
        return "items";
    }

    @Override
    protected Item mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        UUID itemId = resultSet.getObject("item_id", UUID.class);
        String description = resultSet.getString("description");
        UUID userId = resultSet.getObject("user_id", UUID.class);
        UUID auctionId = resultSet.getObject("auction_id", UUID.class);
        boolean active = resultSet.getBoolean("active");

        return new Item(description, userId, auctionId, itemId, active);
    }

    @Override
    protected PreparedStatement createInsertStatement(Item item) throws SQLException {
        String sql = "INSERT INTO items (item_id, description, user_id, active, auction_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, item.getItemID());
        statement.setString(2, item.getDescription());
        statement.setObject(3, item.getUserID());
        statement.setBoolean(4, item.isActive());
        statement.setObject(5, item.getAuctionID());
        return statement;
    }

    @Override
    protected PreparedStatement createUpdateStatement(Item item) throws SQLException {
        String sql = "UPDATE items SET description = ?, active = ? WHERE item_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, item.getDescription());
        statement.setBoolean(2, item.isActive());
        statement.setObject(3, item.getItemID());
        return statement;
    }

    public void finishBidding(UUID itemId) throws SQLException {
        String sql = "UPDATE items SET active = false WHERE item_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, itemId);
            statement.executeUpdate();
        }
    }

    public List<Item> getActiveItems(UUID auctionID) throws SQLException {
        String sql = "SELECT * FROM items WHERE active = true AND auction_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, auctionID);
            ResultSet resultSet = statement.executeQuery();
            List<Item> items = new ArrayList<>();
            while (resultSet.next()) {
                items.add(mapResultSetToEntity(resultSet));
            }
            return items;
        }
    }
}