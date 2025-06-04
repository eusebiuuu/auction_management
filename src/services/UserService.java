package services;

import database.GenericRepository;
import models.*;

import javax.naming.AuthenticationException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserService extends GenericRepository<User> {
    private final HashMap<UUID, User> users;
    private static UserService instance;

    private UserService() throws SQLException {
        super();
        this.users = new HashMap<>();
        try {
            List<User> usersList = this.readAll();
            for (User u : usersList) {
                this.users.put(u.getUserID(), u);
            }
        } catch (SQLException e) {
            throw new SQLException("Users extraction failed: " + e.getMessage());
        }
    }

    public static synchronized UserService getInstance() throws SQLException {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public void addUser(User user) {
        users.put(user.getUserID(), user);
        try {
            this.create(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist user to database: " + e.getMessage());
        }
    }

    public User getUser(UUID userID) {
        User user = users.get(userID);
        if (user == null) {
            throw new NullPointerException("The user with the given ID doesn't exist");
        }
        return user;
    }

    public void createBidder() throws SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert bidder full name:");
        String username = scan.nextLine();
        addUser(new Bidder(username));
    }

    public void createInitiator() throws SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert initiator full name:");
        String username = scan.nextLine();
        addUser(new Initiator(username));
    }

    public void createAdmin() throws SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert admin full name:");
        String username = scan.nextLine();
        addUser(new Admin(username));
    }

    public void createUser() throws SQLException {
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

        if (!(author instanceof Admin) && authorID != userID) {
            throw new AuthenticationException("You are not allow to perform this operation");
        }
        User user = users.get(userID);
        if (users.remove(userID) == null) {
            throw new RuntimeException("No user with the given ID exists");
        }

        try {
            this.delete(authorID);
        } catch (Exception e) {
            users.put(userID, user);
            throw new RuntimeException("Failed to persist user to database: " + e.getMessage());
        }

        System.out.println("User deleted successfully");
    }

    public void showAllCard(UUID userID) {
        TreeSet<Card> cards = getUser(userID).getCards();
        for (Card c : cards) {
            System.out.println(c.toString());
        }
    }

    public void createCard(UUID userID) throws SQLException {
        User user = getUser(userID);

        Scanner scan = new Scanner(System.in);
        System.out.println("Insert card info:");

        System.out.println("Holder name:");
        String holderName = scan.nextLine();

        System.out.println("Expiration month:");
        Integer month = scan.nextInt();

        System.out.println("Expiration year:");
        Integer year = scan.nextInt();

        user.addCard(new Card(holderName, month, year, userID));
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
    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    protected User mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        UUID userId = resultSet.getObject("user_id", UUID.class);
        String fullName = resultSet.getString("full_name");
        UserRole role = UserRole.valueOf(resultSet.getString("role"));

        if (role == UserRole.ADMIN) {
            return new Admin(fullName, userId);
        } else if (role == UserRole.BIDDER) {
            return new Bidder(fullName, userId);
        } else if (role == UserRole.INITIATOR) {
            return new Initiator(fullName, userId);
        } else {
            throw new InputMismatchException("The user role is invalid");
        }
    }

    @Override
    protected PreparedStatement createInsertStatement(User user) throws SQLException {
        String sql = "INSERT INTO users (user_id, full_name, role) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, user.getUserID());
        statement.setString(2, user.getFullName());
        statement.setString(3, user.getRole().toString());
        return statement;
    }

    @Override
    protected PreparedStatement createUpdateStatement(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, role = ? WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, user.getFullName());
        statement.setString(2, user.getRole().toString());
        statement.setObject(3, user.getUserID());
        return statement;
    }

    @Override
    protected PreparedStatement createDeleteStatement(UUID userID) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setObject(1, userID);
        return statement;
    }

    public User findByFullName(String fullName) throws SQLException {
        String sql = "SELECT * FROM users WHERE full_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fullName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToEntity(resultSet);
            }
            return null;
        }
    }
}
