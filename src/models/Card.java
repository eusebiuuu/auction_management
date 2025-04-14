package models;

import java.util.UUID;

public class Card {
    private final UUID code;
    private final String holderName;
    private final Integer expirationMonth;
    private final Integer expirationYear;
    private Double balance;
    private Double blockedSum;

    public Card(String holderName, Integer expirationMonth, Integer expirationYear) {
        this.code = UUID.randomUUID();
        this.holderName = holderName;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.balance = 0.0;
        this.blockedSum = 0.0;
    }

    public UUID getCode() {
        return code;
    }

    public String getHolderName() {
        return holderName;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public Double getBalance() {
        return balance;
    }

    public Double getBlockedSum() {
        return blockedSum;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void setBlockedSum(Double blockedSum) {
        this.blockedSum = blockedSum;
    }

    public String toString() {
        return "Card: " + code + ", " + holderName + ", " + balance + ", " + blockedSum;
    }
}
