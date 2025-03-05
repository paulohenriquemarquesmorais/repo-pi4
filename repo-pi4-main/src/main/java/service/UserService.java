package service;

import dao.UserDAO;
import model.User;
import java.util.List;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // Construtor adicional para testes
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(String username, String password) {
        return userDAO.findByUsernameAndPassword(username, password);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User getUser(int userId) {
        return userDAO.findById(userId);
    }

    public boolean registerUser(User user) {
        // Verificar se username ou email já existem
        User existingUser = userDAO.findByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }

        return userDAO.insert(user);
    }

    public boolean deleteUser(int userId) {
        return userDAO.delete(userId);
    }

    public boolean changeUserPassword(int userId, String newPassword) {
        return userDAO.updatePassword(userId, newPassword);
    }

    public boolean changeUserAccessLevel(int userId, boolean isAdmin) {
        return userDAO.updateAccessLevel(userId, isAdmin);
    }

    public boolean toggleUserActiveStatus(int userId) {
        User user = userDAO.findById(userId);

        if (user == null) {
            return false;
        }

        boolean newStatus = !user.isActive();
        return userDAO.updateActiveStatus(userId, newStatus);
    }
}