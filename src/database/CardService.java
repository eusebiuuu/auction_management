package database;

import models.Auction;
import models.Card;

import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.UUID;

public class CardService extends GenericRepository<Card> {
    private static CardService instance;

    private CardService() {
        super();
    }

    public static synchronized CardService getInstance() {
        if (instance == null) {
            instance = new CardService();
        }
        return instance;
    }

    @Override
    protected String getTableName() {
        return "cards";
    }

    @Override
    protected Card mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        UUID cardId = resultSet.getObject("card_id", UUID.class);
        String holderName = resultSet.getString("holder_name");
        int month = resultSet.getInt("expiration_month");
        int year = resultSet.getInt("expiration_year");
        UUID userId = resultSet.getObject("user_id", UUID.class);
        double balance = resultSet.getDouble("balance");
        double blockedSum = resultSet.getDouble("blocked_sum");

        return new Card(holderName, month, year, userId, blockedSum, balance, cardId);
    }

    @Override
    protected PreparedStatement createInsertStatement(Card card) throws SQLException {
        if (!validateCardExpiration(card)) {
            throw new InputMismatchException("Card is expired");
        }
        String sql = "INSERT INTO cards (card_id, user_id, holder_name, expiration_month, " +
                "expiration_year, balance, blocked_sum) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, card.getCode());
        statement.setObject(2, card.getUserID());
        statement.setString(3, card.getHolderName());
        statement.setInt(4, card.getExpirationMonth());
        statement.setInt(5, card.getExpirationYear());
        statement.setDouble(6, card.getBalance());
        statement.setDouble(7, card.getBlockedSum());
        return statement;
    }

    @Override
    protected PreparedStatement createUpdateStatement(Card card) throws SQLException {
        String sql = "UPDATE cards SET balance = ?, blocked_sum = ? WHERE card_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setDouble(1, card.getBalance());
        statement.setDouble(2, card.getBlockedSum());
        statement.setObject(3, card.getCode());
        return statement;
    }

    @Override
    protected PreparedStatement createDeleteStatement(UUID cardID) throws SQLException {
        String sql = "DELETE FROM cards WHERE card_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, cardID);
        return statement;
    }

    public List<Card> getCardsByUser(UUID userId) throws SQLException {
        String sql = "SELECT * FROM cards WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, userId);
            ResultSet resultSet = statement.executeQuery();
            List<Card> cards = new ArrayList<>();
            while (resultSet.next()) {
                cards.add(mapResultSetToEntity(resultSet));
            }
            return cards;
        }
    }

    public boolean validateCardExpiration(Card card) {
        YearMonth current = YearMonth.now();
        YearMonth expiration = YearMonth.of(card.getExpirationYear(), card.getExpirationMonth());
        return !expiration.isBefore(current);
    }
}