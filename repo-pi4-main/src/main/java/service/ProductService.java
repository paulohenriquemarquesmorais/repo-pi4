package service;

import dao.ProductDAO;
import model.Product;

import java.util.List;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    // Recupera todos os produtos
    public List<Product> getAllProducts() {
        return productDAO.findAllProducts();
    }

    // Registra um novo produto
    public boolean registerProduct(Product product) {
        // Verifica se o produto já existe
        Product existingProduct = productDAO.findByProduct(product.getProduct());
        if (existingProduct != null) {
            return false; // Produto já existe
        }
        return productDAO.insert(product); // Insere o novo produto
    }

    // Recupera um produto pelo ID
    public Product getProduct(int productId) {
        return productDAO.findById(productId);
    }

    // Atualiza um produto
    public boolean updateProduct(Product updatedProduct) {
        Product existingProduct = productDAO.findById(updatedProduct.getId());

        // Se o produto não for encontrado
        if (existingProduct == null) {
            return false; // Retorna false se não encontrar o produto
        }

        // Atualiza os dados do produto
        existingProduct.setProduct(updatedProduct.getProduct());
        existingProduct.setAssessment(updatedProduct.getAssessment());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setQtd(updatedProduct.getQtd());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setActive(updatedProduct.isActive());

        // Salva a atualização no banco de dados
        return productDAO.update(existingProduct);
    }
}
