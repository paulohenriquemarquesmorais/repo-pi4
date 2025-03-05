package integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import model.User;
import service.UserService;
import dao.UserDAO;
import util.TestDatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTest {

    private UserService userService;
    private Connection connection;

    @BeforeAll
    void setUpAll() {
        connection = TestDatabaseConnection.getConnection();
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Cria uma instância do DAO com nosso banco de dados de teste
        UserDAO userDAO = new UserDAO(connection);

        // Criar uma instância do serviço com o DAO real
        userService = new UserService(userDAO);

        // Limpar tabela de usuários antes de cada teste
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM users")) {
            stmt.executeUpdate();
        }
    }

    @AfterAll
    void tearDownAll() {
        TestDatabaseConnection.closeConnection();
    }

    @Test
    void testCompleteUserLifecycle() {
        // 1. Criar usuário
        User newUser = new User();
        newUser.setName("Lifecycle User");
        newUser.setUsername("lifecycleuser");
        newUser.setEmail("lifecycle@test.com");
        newUser.setPassword("Initial123");
        newUser.setAdmin(false);
        newUser.setActive(true);

        assertTrue(userService.registerUser(newUser));

        // 2. Autenticar
        User user = userService.authenticate("lifecycleuser", "Initial123");
        assertNotNull(user);

        // 3. Mudar senha
        assertTrue(userService.changeUserPassword(user.getId(), "Changed123"));

        // 4. Verificar que senha antiga não funciona mais
        assertNull(userService.authenticate("lifecycleuser", "Initial123"));

        // 5. Verificar que nova senha funciona
        user = userService.authenticate("lifecycleuser", "Changed123");
        assertNotNull(user);

        // 6. Desativar usuário
        assertTrue(userService.toggleUserActiveStatus(user.getId()));

        // 7. Verificar que usuário desativado não pode fazer login
        assertNull(userService.authenticate("lifecycleuser", "Changed123"));

        // 8. Reativar usuário
        assertTrue(userService.toggleUserActiveStatus(user.getId()));

        // 9. Verificar que usuário reativado pode fazer login
        assertNotNull(userService.authenticate("lifecycleuser", "Changed123"));
    }
}