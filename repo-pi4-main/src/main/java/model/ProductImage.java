package model;

public class ProductImage {
    private int id;
    private String fileName;
    private String directory;
    private boolean isPrimary;
    private int productId;

    public ProductImage() {
    }

    public ProductImage(int id, String fileName, String directory, boolean isPrimary, int productId) {
        this.id = id;
        this.fileName = fileName;
        this.directory = directory;
        this.isPrimary = isPrimary;
        this.productId = productId;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}