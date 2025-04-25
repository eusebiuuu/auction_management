package services;

import models.*;

import javax.naming.AuthenticationException;
import java.util.*;

public class UserService {
    private final HashMap<UUID, User> users;

    public UserService() {
        this.users = new HashMap<>();
    }

    public void addUser(User user) {
        users.put(user.getUserID(), user);
    }

    public User getUser(UUID userID) {
        User user = users.get(userID);
        if (user == null) {
            throw new NullPointerException("The user with the given ID doesn't exist");
        }
        return user;
    }

    public void createBidder() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert bidder full name:");
        String username = scan.nextLine();
        addUser(new Bidder(username));
    }

    public void createInitiator() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert initiator full name:");
        String username = scan.nextLine();
        addUser(new Initiator(username));
    }

    public void createAdmin() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert admin full name:");
        String username = scan.nextLine();
        addUser(new Admin(username));
    }

    public void createUser() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert user type (0 - admin, 1 - bidder, 2 - initiator):");
        int type = scan.nextInt();
        if (type == 0) {
            createAdmin();
        } else if (type == 1) {
            createBidder();
        } else if (type == 2) {
            createInitiator();
        } else {
            throw new InputMismatchException("Invalid type provided");
        }
    }

    public void removeUser(UUID authorID) throws AuthenticationException {
        User author = getUser(authorID);
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert user ID:");
        UUID userID = UUID.fromString(scan.nextLine());

        if (!(author instanceof Admin) && author.getUserID() != userID) {
            throw new AuthenticationException("You are not allow to perform this operation");
        }
        if (users.remove(author.getUserID()) == null) {
            throw new RuntimeException("No user with the given ID exists");
        }

        System.out.println("User deleted successfully");
    }

    public void showAllCard(UUID userID) {
        TreeSet<Card> cards = getUser(userID).getCards();
        for (Card c : cards) {
            System.out.println(c.toString());
        }
    }

    public void createCard(UUID userID) {
        User user = getUser(userID);

        Scanner scan = new Scanner(System.in);
        System.out.println("Insert card info:");

        System.out.println("Holder name:");
        String holderName = scan.nextLine();

        System.out.println("Expiration month:");
        Integer month = scan.nextInt();

        System.out.println("Expiration year:");
        Integer year = scan.nextInt();

        user.addCard(new Card(holderName, month, year));
    }

    public void showAllUsers(UUID userID) throws AuthenticationException {
        User user = getUser(userID);
        if (!(user instanceof Admin)) {
            throw new AuthenticationException("You cannot perform this operation");
        }

        List<User> users = getAllUsers();
        for (User currentUser : users) {
            System.out.println(currentUser.toString());
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
