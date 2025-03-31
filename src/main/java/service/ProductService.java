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

        // Salva a atualização no banco de dados
        return productDAO.update(existingProduct);
    }

    public boolean updateProductStatus(Product updatedProductStatus) {
        Product existingProduct = productDAO.findById(updatedProductStatus.getId());

        // Se o produto não for encontrado
        if (existingProduct == null) {
            System.out.println("Produto não encontrado: ID " + updatedProductStatus.getId());
            return false; // Retorna false se não encontrar o produto
        }

        // Verificando o status antes da atualização
        System.out.println("Status atual antes da atualização: " + (existingProduct.isActive() ? "Ativo" : "Inativo"));

        // Atualiza o status do produto
        existingProduct.setActive(updatedProductStatus.isActive());

        // Verificando o novo status
        System.out.println("Novo status após alteração: " + (existingProduct.isActive() ? "Ativo" : "Inativo"));

        // Salva a atualização no banco de dados
        boolean isUpdated = productDAO.updateStatus(existingProduct);
        if (isUpdated) {
            System.out.println("Produto atualizado com sucesso no banco de dados.");
        } else {
            System.out.println("Falha ao atualizar o produto no banco de dados.");
        }
        return isUpdated;
    }
}



