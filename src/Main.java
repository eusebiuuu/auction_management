import models.Menu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        Menu menu;
        try {
            menu = Menu.getInstance();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        int command;
        do {
            menu.printMenu();
            try {
                command = scan.nextInt();
            } catch (Exception e) {
                command = 17;
                System.out.println(e.getMessage());
            }
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
                case 13 -> menu.showAllProductBids();
                case 14 -> menu.showAllMadeBids();
                case 15 -> menu.cancelBidding();
                case 16 -> menu.deleteAuction();
                default -> {}
            }
        } while (command != 17);
    }
}