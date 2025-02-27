package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Usando SQLite para simplificar (não precisa de servidor separado)
                String url = "jdbc:sqlite:usermanagement.db";
                connection = DriverManager.getConnection(url);
                System.out.println("Conexão com o banco de dados estabelecida!");
            } catch (SQLException e) {
                System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Conexão com o banco de dados encerrada!");
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão com o banco: " + e.getMessage());
            }
        }
    }
}