package Functions;

import model.Product;
import model.User;
import service.ProductService;
import util.ProductValidator;
import java.util.List;
import java.util.Scanner;

import static Functions.UsersFunctions.loggedUser;

public class ProductsFunctions {

    private static Scanner scanner = new Scanner(System.in);
    private static ProductService productService = new ProductService();

    public static void listProducts() {
        List<Product> products = productService.getAllProducts();

        System.out.println("\n=== Lista de Produtos ===");
        if (products.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
        } else {
            for (Product product : products) {
                System.out.println("ID: " + product.getId() +
                        " | Produto: " + product.getProduct() +
                        " | Avaliação: " + product.getAssessment() +
                        " | Descrição: " + product.getDescription() +
                        " | Quantidade: " + product.getQtd() +
                        " | Preço: " + product.getPrice() +
                        " | Status: " + (product.isActive() ? "Ativo" : "Inativo"));
            }
        }

        // Opções para o usuário
        System.out.println("0. Voltar ao menu inicial");

        // Verifica se o usuário é admin antes de exibir a opção de incluir produto
        if (loggedUser.isAdmin()) {
            System.out.println("i. Incluir novo produto");
        }

        System.out.println("Escolha uma opção ou insira o ID do produto para fazer alteração:");

        String subOption = scanner.nextLine();

        switch (subOption) {
            case "0":
                // Verifica o tipo de usuário e retorna ao menu apropriado
                if (loggedUser.isAdmin()) {
                    Functions.UserMenu.showAdminMenu();
                } else {
                    Functions.UserMenu.showUserMenu();
                }
                break;
            case "i":
                if (loggedUser.isAdmin()) {
                    registerProduct();  // Só pode incluir se for admin
                } else {
                    System.out.println("Você não tem permissão para incluir produtos.");
                }
                break;
            default:
                try {
                    int productId = Integer.parseInt(subOption);
                    Product product = productService.getProduct(productId);

                    if (product == null) {
                        System.out.println("Produto não encontrado!");
                        return;
                    }

                    System.out.println("Alterando produto com ID: " + productId);
                    System.out.println("\n=== Dados do Produto ===");
                    System.out.println("ID: " + product.getId());
                    System.out.println("Produto: " + product.getProduct());
                    System.out.println("Avaliação: " + product.getAssessment());
                    System.out.println("Descrição: " + product.getDescription());
                    System.out.println("Quantidade: " + product.getQtd());
                    System.out.println("Preço: " + product.getPrice());
                    System.out.println("Ativo: " + product.isActive());

                    changeProduct(product);

                } catch (NumberFormatException e) {
                    System.out.println("Opção inválida! Digite '0', 'i' ou um número válido.");
                }
        }
    }


    public static void registerProduct() {
        Product newProduct = new Product();
        System.out.println("\n=== Cadastro de Produto ===");

        // Corrigindo problema na entrada do nome do produto
        String product;
        while (true) {
            System.out.print("Produto: ");
            product = scanner.nextLine().trim();

            if (product.isEmpty()) {
                System.out.println("O nome do produto não pode estar vazio!");
                continue; // Pede novamente
            }

            if (!ProductValidator.isValidProduct(product)) {
                System.out.println("Nome do produto inválido. Tente novamente.");
                continue;
            }

            break; // Sai do loop se a entrada for válida
        }
        newProduct.setProduct(product);

        double assessment;
        while (true) {
            System.out.print("Avaliação (0.0 - 5.0): ");
            if (scanner.hasNextDouble()) {
                assessment = scanner.nextDouble();
                scanner.nextLine(); // Limpa buffer

                if (ProductValidator.isValidAssessment(assessment)) {
                    break;
                }
            } else {
                scanner.next(); // Descarta entrada inválida
            }
            System.out.println("Digite um valor válido para avaliação.");
        }
        newProduct.setAssessment(assessment);

        String description;
        while (true) {
            System.out.print("Descrição: ");
            description = scanner.nextLine().trim();

            // Verificar se a descrição está vazia ou não
            if (description.isEmpty()) {
                System.out.println("A descrição não pode estar vazia! Tente novamente.");
            } else if (ProductValidator.isValidDescription(description)) {
                break; // Se a descrição for válida, sai do loop
            } else {
                System.out.println("Descrição inválida! Tente novamente.");
            }
        }
        newProduct.setDescription(description);

        int qtd;
        while (true) {
            System.out.print("Insira a quantidade: ");
            if (scanner.hasNextInt()) {
                qtd = scanner.nextInt();
                scanner.nextLine(); // Limpa buffer

                if (ProductValidator.isValidQtd(qtd)) {
                    break;
                }
            } else {
                scanner.next(); // Descarta entrada inválida
            }
            System.out.println("Digite um número inteiro válido.");
        }
        newProduct.setQtd(qtd);

        double price;
        while (true) {
            System.out.print("Insira o preço: ");
            if (scanner.hasNextDouble()) {
                price = scanner.nextDouble();
                scanner.nextLine(); // Limpa buffer

                if (ProductValidator.isValidPrice(price)) {
                    break;
                }
            } else {
                scanner.next(); // Descarta entrada inválida
            }
            System.out.println("Digite um valor numérico válido.");
        }
        newProduct.setPrice(price);

        newProduct.setActive(true); // Novos produtos são ativos por padrão

        System.out.println("Tentando inserir o produto: " + newProduct.getProduct());

        boolean success = productService.registerProduct(newProduct);

        if (success) {
            System.out.println("Produto cadastrado com sucesso!");
        } else {
            System.out.println("Erro ao cadastrar produto. Produto já existe.");
        }

        listProducts(); // Volta para a lista de produtos após cadastro
    }

    public static void changeProduct(Product product) {
        System.out.println("\n=== Alteração de Produto ===");

        if (loggedUser.isAdmin()) {
            // Alterar o nome do produto
            System.out.println("Produto atual: " + product.getProduct());
            System.out.print("Novo nome do produto (deixe em branco para não alterar): ");
            String newProductName = scanner.nextLine().trim();
            if (!newProductName.isEmpty()) {
                if (ProductValidator.isValidProduct(newProductName)) {
                    product.setProduct(newProductName);
                } else {
                    System.out.println("Nome do produto inválido.");
                    return;
                }
            }

            // Alterar a avaliação
            System.out.println("Avaliação atual: " + product.getAssessment());
            System.out.print("Nova avaliação (0.0 - 5.0, deixe em branco para não alterar): ");
            String assessmentInput = scanner.nextLine().trim();
            if (!assessmentInput.isEmpty()) {
                double newAssessment = Double.parseDouble(assessmentInput);
                if (ProductValidator.isValidAssessment(newAssessment)) {
                    product.setAssessment(newAssessment);
                } else {
                    System.out.println("Avaliação inválida.");
                    return;
                }
            }

            // Alterar a descrição
            System.out.println("Descrição atual: " + product.getDescription());
            System.out.print("Nova descrição (deixe em branco para não alterar): ");
            String newDescription = scanner.nextLine().trim();
            if (!newDescription.isEmpty()) {
                if (ProductValidator.isValidDescription(newDescription)) {
                    product.setDescription(newDescription);
                } else {
                    System.out.println("Descrição inválida.");
                    return;
                }
            }

            // Alterar a quantidade
            System.out.println("Quantidade atual: " + product.getQtd());
            System.out.print("Nova quantidade (deixe em branco para não alterar): ");
            String qtdInput = scanner.nextLine().trim();
            if (!qtdInput.isEmpty()) {
                int newQtd = Integer.parseInt(qtdInput);
                if (ProductValidator.isValidQtd(newQtd)) {
                    product.setQtd(newQtd);
                } else {
                    System.out.println("Quantidade inválida.");
                    return;
                }
            }

            // Alterar o preço
            System.out.println("Preço atual: " + product.getPrice());
            System.out.print("Novo preço (deixe em branco para não alterar): ");
            String priceInput = scanner.nextLine().trim();
            if (!priceInput.isEmpty()) {
                double newPrice = Double.parseDouble(priceInput);
                if (ProductValidator.isValidPrice(newPrice)) {
                    product.setPrice(newPrice);
                } else {
                    System.out.println("Preço inválido.");
                    return;
                }
            }

            // Alterar o status (opção contrária)
            System.out.println("Status atual: " + (product.isActive() ? "Ativo" : "Inativo"));
            String statusMessage = product.isActive() ? "Desativar Produto" : "Ativar Produto";
            System.out.print(statusMessage + "? (Y/N): ");
            String statusInput = scanner.nextLine().trim();

            if (statusInput.equalsIgnoreCase("Y")) {
                product.setActive(!product.isActive());
            }

            // Salvar as alterações
            System.out.println("Deseja salvar as alterações? (Y/N): ");
            String saveInput = scanner.nextLine().trim();
            if (saveInput.equalsIgnoreCase("Y")) {
                boolean success = productService.updateProduct(product);
                if (success) {
                    System.out.println("Produto alterado com sucesso!");
                    listProducts();  // Retorna à lista de produtos
                } else {
                    System.out.println("Erro ao alterar produto.");
                }
            } else if (saveInput.equalsIgnoreCase("N")) {
                System.out.println("Alterações não salvas. Voltando ao menu de jogos...");
                listProducts();  // Retorna à lista de produtos ou menu anterior
            }
        } else {
            // Caso não seja admin, permitir apenas a alteração da quantidade
            System.out.println("Quantidade atual: " + product.getQtd());
            System.out.print("Nova quantidade (deixe em branco para não alterar): ");
            String qtdInput = scanner.nextLine().trim();
            if (!qtdInput.isEmpty()) {
                int newQtd = Integer.parseInt(qtdInput);
                if (ProductValidator.isValidQtd(newQtd)) {
                    product.setQtd(newQtd);
                } else {
                    System.out.println("Quantidade inválida.");
                    return;
                }
            }

            // Salvar as alterações
            System.out.println("Deseja salvar as alterações? (Y/N): ");
            String saveInput = scanner.nextLine().trim();
            if (saveInput.equalsIgnoreCase("Y")) {
                boolean success = productService.updateProduct(product);
                if (success) {
                    System.out.println("Produto alterado com sucesso!");
                    listProducts();  // Retorna à lista de produtos
                } else {
                    System.out.println("Erro ao alterar produto.");
                }
            } else if (saveInput.equalsIgnoreCase("N")) {
                System.out.println("Alterações não salvas. Voltando ao menu de jogos...");
                listProducts();  // Retorna à lista de produtos ou menu anterior
            }
        }
    }
}

