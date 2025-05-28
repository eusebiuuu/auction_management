package models;

import database.CardService;

import java.sql.SQLException;
import java.util.*;

public class User {
    private final UUID userID;
    private final String fullName;
    private final TreeSet<Card> cards;
    private final UserRole role;
    CardService cardService = CardService.getInstance();

    public User(String fullName, UserRole role) {
        userID = UUID.randomUUID();
        this.fullName = fullName;
        this.role = role;
        cards = new TreeSet<>(Comparator.comparingDouble(Card::getBalance));
    }

    public User(String fullName, UserRole role, UUID userID) throws SQLException {
        this.userID = userID;
        this.fullName = fullName;
        this.role = role;
        cards = new TreeSet<>(Comparator.comparingDouble(Card::getBalance));
        List<Card> cardsList = cardService.getCardsByUser(this.userID);
        cards.addAll(cardsList);
    }

    public UUID getUserID() {
        return userID;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isBidder() {
        return role == UserRole.BIDDER;
    }

    public boolean isInitiator() {
        return role == UserRole.INITIATOR;
    }

    public Card getCard(UUID cardID) {
        Card card = null;
        for (Card c : cards) {
            if (c.getCode().equals(cardID)) {
                card = c;
                break;
            }
        }

        if (card == null) {
            throw new NullPointerException("Card doesn't exist");
        }

        return card;
    }

    public void updateBlockedSum(UUID cardID, double sum, int type) throws SQLException {
        Card card = getCard(cardID);
        card.setBlockedSum(card.getBlockedSum() + type * sum);
    }

    public void updateBlockedSumNoTransactions(UUID cardID, double sum, int type) throws SQLException {
        Card card = getCard(cardID);
        card.setBlockedSumNoTransactions(card.getBlockedSum() + type * sum);
    }

    public void addCard(Card newCard) throws SQLException {
        try {
            cardService.create(newCard);
            cards.add(newCard);
        } catch (SQLException e) {
            throw new SQLException("Card creation failed: " + e.getMessage());
        }
    }

    public void removeCard(UUID cardID) throws SQLException {
        try {
            cardService.delete(cardID);
            this.cards.removeIf(card -> card.getCode().equals(cardID));
        } catch (SQLException e) {
            throw new SQLException("Card removal failed: " + e.getMessage());
        }
    }

    public TreeSet<Card> getCards() {
        return cards;
    }

    public String toString() {
        return "User: " + userID + ", " + fullName;
    }

    public UserRole getRole() {
        return role;
    }
}
