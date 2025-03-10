package service;

import dao.ProductImageDAO;
import model.ProductImage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ProductImageService {
    private ProductImageDAO productImageDAO;
    private static final String BASE_DIRECTORY = "imagens/";

    public ProductImageService() {
        this.productImageDAO = new ProductImageDAO();
    }

    public List<ProductImage> getProductImages(int productId) {
        return productImageDAO.findByProductId(productId);
    }

    public boolean addProductImage(String fileName, String sourceDirectory, boolean isPrimary, int productId) {
        try {
            // Criar diretório de destino se não existir
            String destDirectory = BASE_DIRECTORY + productId + "/";
            File directory = new File(destDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Copiar arquivo para diretório de destino
            Path sourcePath = Paths.get(sourceDirectory, fileName);
            Path targetPath = Paths.get(destDirectory, fileName);
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Salvar informações no banco de dados
            ProductImage image = new ProductImage();
            image.setFileName(fileName);
            image.setDirectory(destDirectory);
            image.setPrimary(isPrimary);
            image.setProductId(productId);

            return productImageDAO.insert(image);
        } catch (Exception e) {
            System.out.println("Erro ao adicionar imagem: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProductImage(int imageId) {
        ProductImage image = productImageDAO.findById(imageId);
        if (image == null) {
            return false;
        }

        try {
            // Excluir arquivo físico
            Path filePath = Paths.get(image.getDirectory(), image.getFileName());
            Files.deleteIfExists(filePath);

            // Excluir registro no banco
            return productImageDAO.delete(imageId);
        } catch (Exception e) {
            System.out.println("Erro ao excluir imagem: " + e.getMessage());
            return false;
        }
    }

    public boolean setImageAsPrimary(int imageId) {
        return productImageDAO.updatePrimaryStatus(imageId, true);
    }
}