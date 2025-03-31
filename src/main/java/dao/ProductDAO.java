package dao;

import model.Product;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() {
        this.connection = DatabaseConnection.getConnection();
        createTableIfNotExists();
    }

    // Construtor adicional para testes unitários
    public ProductDAO(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product TEXT NOT NULL, " +
                "assessment REAL NOT NULL," +
                "description TEXT, " +
                "qtd INTEGER NOT NULL, " +
                "price REAL NOT NULL, " +
                "active BOOLEAN NOT NULL" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);

            // Verificar se a coluna 'description' já existe, caso contrário, adicionar a coluna
            try {
                stmt.execute("SELECT description FROM products LIMIT 1");
            } catch (SQLException e) {
                // A coluna não existe, adicionar a coluna
                stmt.execute("ALTER TABLE products ADD COLUMN description TEXT");
            }

            // Criar produto padrão se não existir nenhum produto
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO products (product, assessment, description, qtd, price, active) VALUES ('Call Of Duty', 5.0, 'Jogo de tiro', 20, 150.00, 1)");
                System.out.println("Produto padrão criado!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao criar/atualizar tabela de produtos: " + e.getMessage());
        }
    }

    public Product findByProduct(String product) {
        String sql = "SELECT * FROM products WHERE product = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto por nome: " + e.getMessage());
        }
        return null;
    }

    public List<Product> findAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products " +
                "ORDER BY ID DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar todos os produtos: " + e.getMessage());
        }
        return products;
    }

    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setProduct(rs.getString("product"));
        product.setDescription(rs.getString("description"));
        product.setAssessment(rs.getDouble("assessment"));
        product.setQtd(rs.getInt("qtd"));
        product.setPrice(rs.getDouble("price"));
        product.setActive(rs.getBoolean("active"));
        return product;
    }

    public boolean insert(Product product) {
        String sql = "INSERT INTO products (product, assessment, description, qtd, price, active) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getProduct());         // Produto
            pstmt.setDouble(2, product.getAssessment());       //Avaliação
            pstmt.setString(3, product.getDescription());    // Descrição
            pstmt.setInt(4, product.getQtd());               // Quantidade
            pstmt.setDouble(5, product.getPrice());          // Preço
            pstmt.setBoolean(6, product.isActive());         // Status

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir produto: " + e.getMessage());
            return false;
        }
    }

    public Product findById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto por ID: " + e.getMessage());
        }

        return null;
    }

    public boolean update(Product product) {
        String sql = "UPDATE products SET product = ?, assessment = ?, description = ?, qtd = ?, price = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getProduct());         // Produto
            pstmt.setDouble(2, product.getAssessment());       // Avaliação
            pstmt.setString(3, product.getDescription());     // Descrição
            pstmt.setInt(4, product.getQtd());                // Quantidade
            pstmt.setDouble(5, product.getPrice());           // Preço
            pstmt.setInt(6, product.getId());                 // ID do produto (para identificar qual produto será atualizado)

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Retorna true se a atualização foi bem-sucedida
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar produto: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(Product product) {
        String sql = "UPDATE products SET active = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Passa 1 para Ativo e 0 para Inativo
            pstmt.setInt(1, product.isActive() ? 1 : 0);  // Atualizando com valores 1 ou 0
            pstmt.setInt(2, product.getId());  // ID do produto (para identificar qual produto será atualizado)

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Retorna true se a atualização foi bem-sucedida
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar produto: " + e.getMessage());
            return false;
        }
    }

}
