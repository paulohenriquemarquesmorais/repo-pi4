package dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.User;
import util.TestDatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserDAOTest {

    private UserDAO userDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Configurar conexão com banco de dados em memória para testes
        connection = TestDatabaseConnection.getConnection();
        userDAO = new UserDAO(connection);

        // Limpar tabela de usuários antes de cada teste
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM users")) {
            stmt.executeUpdate();
        }

        // Inserir dados de teste
        User testUser = new User();
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setAdmin(false);
        testUser.setActive(true);

        userDAO.insert(testUser);
    }

    @Test
    void testFindByUsername() {
        // Act
        User result = userDAO.findByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testFindByUsernameAndPassword_Success() {
        // Act
        User result = userDAO.findByUsernameAndPassword("testuser", "password123");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testFindByUsernameAndPassword_WrongPassword() {
        // Act
        User result = userDAO.findByUsernameAndPassword("testuser", "wrongpassword");

        // Assert
        assertNull(result);
    }

    @Test
    void testUpdateActiveStatus() {
        // Arrange
        User user = userDAO.findByUsername("testuser");
        assertTrue(user.isActive());

        // Act
        boolean result = userDAO.updateActiveStatus(user.getId(), false);

        // Assert
        assertTrue(result);

        User updatedUser = userDAO.findByUsername("testuser");
        assertFalse(updatedUser.isActive());

        // Verificar que um usuário inativo não pode fazer login
        User authAttempt = userDAO.findByUsernameAndPassword("testuser", "password123");
        assertNull(authAttempt);
    }
}