import models.Menu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Menu menu = Menu.getInstance();
        int command;
        do {
            menu.printMenu();
            command = scan.nextInt();
            switch (command) {
                case 1 -> menu.createUser();
                case 2 -> menu.deleteUser();
                case 3 -> menu.showAllUsers();
                case 4 -> menu.showAllCards();
                case 5 -> menu.addCard();
                case 6 -> menu.createAuction();
                case 7 -> menu.showAllAuctions();
                case 8 -> menu.showAllItems();
                case 9 -> menu.addItem();
                case 10 -> menu.finishBidding();
                case 11 -> menu.makeBid();
                case 12 -> menu.addSumToCard();
                default -> {}
            }
        } while (command != 13);
    }
}