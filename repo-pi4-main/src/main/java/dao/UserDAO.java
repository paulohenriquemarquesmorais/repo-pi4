package dao;

import model.User;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
        createTableIfNotExists();
    }

    // Construtor adicional para testes unitários
    public UserDAO(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        // Modificar a criação da tabela para incluir o campo active
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "username TEXT NOT NULL UNIQUE, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "admin BOOLEAN NOT NULL, " +
                "active BOOLEAN NOT NULL DEFAULT 1" +
                "); ";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);

            // Verificar se a coluna 'active' já existe
            try {
                stmt.execute("SELECT active FROM users LIMIT 1");
            } catch (SQLException e) {
                // A coluna não existe, adicionar a coluna
                stmt.execute("ALTER TABLE users ADD COLUMN active BOOLEAN NOT NULL DEFAULT 1");
            }

            // Criar usuário admin padrão se não existir nenhum usuário
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdmin = "INSERT INTO users (name, username, email, password, admin, active) " +
                        "VALUES ('Administrador', 'admin', 'admin@sistema.com', 'admin123', 1, 1), " +
                        "('Estoquista', 'estoq', 'estoq@gmail.com', 'estoq123', 0, 1)";
                stmt.execute(insertAdmin);
                System.out.println("Usuário administrador padrão criado!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao criar/atualizar tabela de usuários: " + e.getMessage());
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário por username: " + e.getMessage());
        }

        return null;
    }

    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND active = 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar usuário: " + e.getMessage());
        }

        return null;
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }

        return null;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar todos os usuários: " + e.getMessage());
        }

        return users;
    }

    public boolean insert(User user) {
        String sql = "INSERT INTO users (name, username, email, password, admin, active) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setBoolean(5, user.isAdmin());
            pstmt.setBoolean(6, user.isActive());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET name = ?, username = ?, email = ?, password = ?, admin = ?, active = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setBoolean(5, user.isAdmin());
            pstmt.setBoolean(6, user.isActive());
            pstmt.setInt(7, user.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar senha: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAccessLevel(int userId, boolean isAdmin) {
        String sql = "UPDATE users SET admin = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, isAdmin);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar nível de acesso: " + e.getMessage());
            return false;
        }
    }

    public boolean updateActiveStatus(int userId, boolean isActive) {
        String sql = "UPDATE users SET active = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status do usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir usuário: " + e.getMessage());
            return false;
        }
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setAdmin(rs.getBoolean("admin"));
        user.setActive(rs.getBoolean("active"));
        return user;
    }

}