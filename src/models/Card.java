package models;

import database.CardService;
import utils.Checker;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.InputMismatchException;
import java.util.UUID;

public class Card {
    private final UUID code;
    private final String holderName;
    private final Integer expirationMonth;
    private final Integer expirationYear;
    private Double balance;
    private Double blockedSum;
    private final UUID userID;
    private final CardService cardService = CardService.getInstance();

    public Card(String holderName, Integer expirationMonth, Integer expirationYear, UUID userID) {
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        checkCardValidity();

        this.code = UUID.randomUUID();
        this.holderName = holderName;

        this.balance = 0.0;
        this.blockedSum = 0.0;
        this.userID = userID;
    }

    public Card(String holderName, Integer expirationMonth, Integer expirationYear,
                UUID userID, double blockedSum, double balance, UUID code) {
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.code = code;
        this.holderName = holderName;
        this.balance = balance;
        this.blockedSum = blockedSum;
        this.userID = userID;
    }

    public void checkCardValidity() {
        YearMonth currentTime = YearMonth.now();
        if (!Checker.isInRange(expirationMonth, 1, 12)) {
            throw new InputMismatchException("Invalid month");
        }
        if (currentTime.getYear() > expirationYear) {
            throw new InputMismatchException("Card expired");
        }
        if (expirationYear == currentTime.getYear() && expirationMonth <= currentTime.getMonthValue()) {
            throw new InputMismatchException("Card expired");
        }
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

    public void setBalance(Double balance) throws SQLException {
        if (balance < 0) {
            throw new InputMismatchException("You cannot have debt in this type of card");
        }
        if (balance < blockedSum) {
            throw new InputMismatchException("New balance is not allowed since you have a bigger blocked sum");
        }
        double oldBalance = this.balance;
        try {
            this.balance = balance;
            cardService.update(this);
        } catch (SQLException e) {
            this.balance = oldBalance;
            throw new SQLException("Balance update failed: " + e.getMessage());
        }
    }

    public void setBlockedSum(Double blockedSum) throws SQLException {
        if (blockedSum < 0) {
            throw new RuntimeException("System error - blockedSum is negative. Please try again later");
        }

        if (blockedSum > balance) {
            System.out.println(this);
            throw new InputMismatchException("You don't have enough available money to bid on this card");
        }

        double oldSum = this.blockedSum;
        try {
            this.blockedSum = blockedSum;
            cardService.update(this);
        } catch (SQLException e) {
            this.blockedSum = oldSum;
            throw new SQLException("Blocked sum update failed: " + e.getMessage());
        }
    }

    public void setBlockedSumNoTransactions(Double blockedSum) {
        if (blockedSum < 0) {
            throw new RuntimeException("System error. Please try again later");
        }

        if (blockedSum > balance) {
            throw new InputMismatchException("You don't have enough available money to bid on this card");
        }
    }

    public String toString() {
        return "Card: " + code + ", " + holderName + ", " + balance + ", " + blockedSum;
    }

    public UUID getUserID() {
        return userID;
    }
}
