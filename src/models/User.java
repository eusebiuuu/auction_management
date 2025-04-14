package models;

import java.util.*;

public class User {
    private final UUID userID;
    private final String fullName;
    private final TreeSet<Card> cards;

    public User(String fullName) {
        userID = UUID.randomUUID();
        this.fullName = fullName;
        cards = new TreeSet<>(Comparator.comparingDouble(Card::getBalance));
    }

    public UUID getUserID() {
        return userID;
    }

    public String getFullName() {
        return fullName;
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

    public void updateBlockedSum(UUID cardID, double sum, int type) {
        Card card = getCard(cardID);

        if (card.getBlockedSum() + sum * type < 0) {
            throw new RuntimeException("System error. Please try again later");
        }

        double currentBlockedSum = card.getBlockedSum();
        card.setBlockedSum(currentBlockedSum + type * sum);
    }

    public void addCard(Card newCard) {
        cards.add(newCard);
    }

    public void removeCard(UUID cardID) {
        this.cards.removeIf(card -> card.getCode().equals(cardID));
    }

    public TreeSet<Card> getCards() {
        return cards;
    }

    public String toString() {
        return "User: " + userID + ", " + fullName;
    }
}
