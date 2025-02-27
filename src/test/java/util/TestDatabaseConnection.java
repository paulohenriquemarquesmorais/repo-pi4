package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Usar banco de dados SQLite em memória para testes
                String url = "jdbc:sqlite::memory:";
                connection = DriverManager.getConnection(url);

                // Criar a tabela de usuários
                try (Statement stmt = connection.createStatement()) {
                    String sql = "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "username TEXT NOT NULL UNIQUE," +
                            "email TEXT NOT NULL UNIQUE," +
                            "password TEXT NOT NULL," +
                            "admin BOOLEAN NOT NULL," +
                            "active BOOLEAN NOT NULL DEFAULT 1" +
                            ")";
                    stmt.execute(sql);
                }

                System.out.println("Conexão de teste com o banco de dados estabelecida!");
            } catch (SQLException e) {
                System.out.println("Erro ao conectar ao banco de dados de teste: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Conexão de teste com o banco de dados encerrada!");
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão de teste com o banco: " + e.getMessage());
            }
        }
    }
}
