package util;

public class ProductValidator {

    public static boolean isValidProduct(String product) {
        return product.length() >= 3 && product.length() <= 200 && product.matches("[a-zA-Z0-9_ ]+");
    }

    public static boolean isValidDescription(String description) {
        return description.length() >= 3 && description.length() <= 2000 && description.matches("[a-zA-Z0-9_ \\.,;!?áàãâéêíóôõúçÁÀÃÂÉÊÍÓÔÕÚÇ]+");
    }

    public static boolean isValidQtd(Integer qtd) {
        return qtd != null && qtd >= 0;
    }

    public static boolean isValidPrice(Double price) {
        return price != null && price >= 0;
    }

    public static boolean isValidAssessment(Double assessment) {
        return assessment != null && assessment >= 1 && assessment <= 5 && assessment % 0.5 == 0;
    }
}

