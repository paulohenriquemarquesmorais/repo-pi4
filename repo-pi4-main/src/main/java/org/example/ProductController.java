package org.example;
import dao.ProductDAO;
import model.Product;

import java.util.List;

import static spark.Spark.*;

public class ProductController {
    public static void startServer() {
        port(3030); // Essa é a porta que o front está esperando!

        get("/products", (req, res) -> {
            res.type("application/json");
            ProductDAO productDAO = new ProductDAO();
            List<Product> produtos = productDAO.findAll();
            return new com.google.gson.Gson().toJson(produtos);
        });
    }
}
