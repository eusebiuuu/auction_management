package models;

import utils.Checker;

import java.time.Month;
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

    public Card(String holderName, Integer expirationMonth, Integer expirationYear) {
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        checkCardValidity();

        this.code = UUID.randomUUID();
        this.holderName = holderName;

        this.balance = 0.0;
        this.blockedSum = 0.0;
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

    public void setBalance(Double balance) {
        if (balance < 0) {
            throw new InputMismatchException("You cannot have debt in this type of card");
        }
        if (balance < blockedSum) {
            throw new InputMismatchException("New balance is not allowed since you have a bigger blocked sum");
        }
        this.balance = balance;
    }

    public void setBlockedSum(Double blockedSum) {
        if (blockedSum < 0) {
            throw new RuntimeException("System error. Please try again later");
        }

        if (blockedSum > balance) {
            throw new InputMismatchException("You don't have enough available money to bid on this card");
        }

        this.blockedSum = blockedSum;
    }

    public String toString() {
        return "Card: " + code + ", " + holderName + ", " + balance + ", " + blockedSum;
    }
}
