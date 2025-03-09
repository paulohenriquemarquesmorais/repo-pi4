package dao;

import model.ProductImage;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductImageDAO {
    private Connection connection;

    public ProductImageDAO() {
        this.connection = DatabaseConnection.getConnection();
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS product_images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "file_name TEXT NOT NULL, " +
                "directory TEXT NOT NULL, " +
                "is_primary BOOLEAN NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela de imagens: " + e.getMessage());
        }
    }

    public boolean insert(ProductImage image) {
        String sql = "INSERT INTO product_images (file_name, directory, is_primary, product_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, image.getFileName());
            pstmt.setString(2, image.getDirectory());
            pstmt.setBoolean(3, image.isPrimary());
            pstmt.setInt(4, image.getProductId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir imagem: " + e.getMessage());
            return false;
        }
    }

    public List<ProductImage> findByProductId(int productId) {
        List<ProductImage> images = new ArrayList<>();
        String sql = "SELECT * FROM product_images WHERE product_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ProductImage image = new ProductImage();
                image.setId(rs.getInt("id"));
                image.setFileName(rs.getString("file_name"));
                image.setDirectory(rs.getString("directory"));
                image.setPrimary(rs.getBoolean("is_primary"));
                image.setProductId(rs.getInt("product_id"));
                images.add(image);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar imagens do produto: " + e.getMessage());
        }

        return images;
    }

    public boolean delete(int imageId) {
        String sql = "DELETE FROM product_images WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, imageId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir imagem: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePrimaryStatus(int imageId, boolean isPrimary) {
        // Se está definindo como primária, primeiro desmarca todas as outras imagens do produto
        if (isPrimary) {
            ProductImage image = findById(imageId);
            if (image != null) {
                resetPrimaryForProduct(image.getProductId());
            }
        }

        String sql = "UPDATE product_images SET is_primary = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, isPrimary);
            pstmt.setInt(2, imageId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status da imagem: " + e.getMessage());
            return false;
        }
    }

    private void resetPrimaryForProduct(int productId) {
        String sql = "UPDATE product_images SET is_primary = 0 WHERE product_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao resetar imagens primárias: " + e.getMessage());
        }
    }

    public ProductImage findById(int imageId) {
        String sql = "SELECT * FROM product_images WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, imageId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ProductImage image = new ProductImage();
                image.setId(rs.getInt("id"));
                image.setFileName(rs.getString("file_name"));
                image.setDirectory(rs.getString("directory"));
                image.setPrimary(rs.getBoolean("is_primary"));
                image.setProductId(rs.getInt("product_id"));
                return image;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar imagem: " + e.getMessage());
        }

        return null;
    }
}