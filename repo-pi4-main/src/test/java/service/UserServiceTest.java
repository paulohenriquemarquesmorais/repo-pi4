package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.UserDAO;
import model.User;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Configurar usuário de teste normal
        testUser = new User();
        testUser.setId(1);
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setAdmin(false);
        testUser.setActive(true);

        // Configurar usuário administrador
        adminUser = new User();
        adminUser.setId(2);
        adminUser.setName("Admin User");
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("admin123");
        adminUser.setAdmin(true);
        adminUser.setActive(true);
    }

    @Test
    void testAuthenticate_Success() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        when(userDAO.findByUsernameAndPassword(username, password)).thenReturn(testUser);

        // Act
        User result = userService.authenticate(username, password);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userDAO, times(1)).findByUsernameAndPassword(username, password);
    }

    @Test
    void testAuthenticate_Failure() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";
        when(userDAO.findByUsernameAndPassword(username, password)).thenReturn(null);

        // Act
        User result = userService.authenticate(username, password);

        // Assert
        assertNull(result);
        verify(userDAO, times(1)).findByUsernameAndPassword(username, password);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(testUser);
        expectedUsers.add(adminUser);

        when(userDAO.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(adminUser.getId(), result.get(1).getId());
        verify(userDAO, times(1)).findAll();
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        User newUser = new User();
        newUser.setName("New User");
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("newpass123");

        when(userDAO.findByUsername("newuser")).thenReturn(null);
        when(userDAO.insert(any(User.class))).thenReturn(true);

        // Act
        boolean result = userService.registerUser(newUser);

        // Assert
        assertTrue(result);
        verify(userDAO, times(1)).findByUsername("newuser");
        verify(userDAO, times(1)).insert(newUser);
    }

    @Test
    void testRegisterUser_DuplicateUsername() {
        // Arrange
        User newUser = new User();
        newUser.setName("New User");
        newUser.setUsername("testuser"); // Duplicate username
        newUser.setEmail("new@example.com");
        newUser.setPassword("newpass123");

        when(userDAO.findByUsername("testuser")).thenReturn(testUser);

        // Act
        boolean result = userService.registerUser(newUser);

        // Assert
        assertFalse(result);
        verify(userDAO, times(1)).findByUsername("testuser");
        verify(userDAO, never()).insert(any(User.class));
    }

    @Test
    void testToggleUserActiveStatus_Success() {
        // Arrange
        int userId = 1;
        when(userDAO.findById(userId)).thenReturn(testUser);
        when(userDAO.updateActiveStatus(userId, false)).thenReturn(true);

        // Act
        boolean result = userService.toggleUserActiveStatus(userId);

        // Assert
        assertTrue(result);
        verify(userDAO, times(1)).findById(userId);
        verify(userDAO, times(1)).updateActiveStatus(userId, false);
    }

    @Test
    void testToggleUserActiveStatus_UserNotFound() {
        // Arrange
        int userId = 999; // Non-existent ID
        when(userDAO.findById(userId)).thenReturn(null);

        // Act
        boolean result = userService.toggleUserActiveStatus(userId);

        // Assert
        assertFalse(result);
        verify(userDAO, times(1)).findById(userId);
        verify(userDAO, never()).updateActiveStatus(anyInt(), anyBoolean());
    }
}