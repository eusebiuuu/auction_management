package services;

import models.User;

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
    public boolean removeUser(UUID userId) {
        return users.remove(userId) != null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
