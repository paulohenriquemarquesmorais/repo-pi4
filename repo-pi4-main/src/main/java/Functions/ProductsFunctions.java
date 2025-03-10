package Functions;

import model.Product;
import service.ProductService;
import util.ProductValidator;
import model.ProductImage;
import service.ProductImageService;

import java.util.InputMismatchException;
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

                    if (loggedUser.isAdmin()) {
                        System.out.println("=== Opções de Produto ===");
                        System.out.println("1. Editar Produto");
                        System.out.println("2. Imagens do Produto");
                        // Exibe a opção com base no status do produto
                        System.out.println("3. " + (product.isActive() ? "Desativar Produto" : "Ativar Produto"));

                        int option = scanner.nextInt();
                        scanner.nextLine(); // Limpa o buffer de entrada

                        switch (option) {
                            case 1:
                                System.out.println("Alterando produto com ID: " + productId);
                                System.out.println("\n=== Dados do Produto ===");
                                System.out.println("ID: " + product.getId());
                                System.out.println("Produto: " + product.getProduct());
                                System.out.println("Avaliação: " + product.getAssessment());
                                System.out.println("Descrição: " + product.getDescription());
                                System.out.println("Quantidade: " + product.getQtd());

                                changeProduct(product);
                                break;

                            case 2:
                                System.out.println("Gerenciando imagens do produto com ID: " + product.getId());
                                manageProductImages(product.getId());
                                break;

                            case 3:
                                // Exibindo a opção correta de ativação ou desativação
                                System.out.println((product.isActive() ? "Desativando produto com ID: " : "Ativando produto com ID: ") + productId);
                                System.out.println("\n=== Dados do Produto ===");
                                System.out.println("ID: " + product.getId());
                                System.out.println("Produto: " + product.getProduct());
                                System.out.println("Avaliação: " + product.getAssessment());
                                System.out.println("Descrição: " + product.getDescription());
                                System.out.println("Quantidade: " + product.getQtd());

                                // Chama a função de mudança de status
                                changeStatus(product);
                                break;
                        }
                    }
                    else {
                        System.out.println("=== Opções de Produto ===");
                        System.out.println("1. Editar Quantidade do Produto");

                        try {
                            int estoqOption = scanner.nextInt();
                            scanner.nextLine(); // Limpa o buffer de entrada

                            switch (estoqOption) {
                                case 1:
                                    System.out.println("Alterando produto com ID: " + productId);
                                    System.out.println("\n=== Dados do Produto ===");
                                    System.out.println("ID: " + product.getId());
                                    System.out.println("Produto: " + product.getProduct());
                                    System.out.println("Avaliação: " + product.getAssessment());
                                    System.out.println("Descrição: " + product.getDescription());
                                    System.out.println("Quantidade: " + product.getQtd());

                                    changeProduct(product);
                                    break;
                                default:
                                    System.out.println("Opção inválida!");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Opção inválida! Digite um número válido.");
                            scanner.nextLine(); // Limpa o buffer para evitar loop infinito
                            listProducts();
                        }
                    }


                } catch (NumberFormatException e) {
                    System.out.println("Opção inválida! Digite '0', 'i' ou um número válido.");
                    listProducts();
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

    public static void changeStatus(Product product) {
        String statusMessage = product.isActive() ? "Desativar Produto" : "Ativar Produto";
        String statusInput = "";

        while (true) {
            System.out.print(statusMessage + "? (Y/N): ");
            statusInput = scanner.nextLine().trim();

            if (statusInput.equalsIgnoreCase("Y")) {
                // Alterando o status
                product.setActive(!product.isActive());  // Alterna o status do produto

                // Chama a função de atualização de status
                boolean isUpdated = productService.updateProductStatus(product); // Atualiza o status no banco ou sistema

                if (isUpdated) {
                    System.out.println("Status do produto alterado com sucesso!");
                } else {
                    System.out.println("Erro ao atualizar o status do produto.");
                }
                break;
            } else if (statusInput.equalsIgnoreCase("N")) {
                System.out.println("Nenhuma alteração realizada.");
                break;
            } else {
                System.out.println("Entrada inválida. Digite 'Y' para confirmar ou 'N' para cancelar.");
            }
        }

        // Agora chamamos a lista de produtos novamente, já com o status atualizado
        // Recarrega a lista de produtos
        System.out.println("\nAção concluída. Retornando ao menu do produto.");
        listProducts();  // Volta para o menu de lista de produtos após a mudança
    }
    private static ProductImageService productImageService = new ProductImageService();

    public static void manageProductImages(int productId) {
        Product product = productService.getProduct(productId);
        if (product == null) {
            System.out.println("Produto não encontrado!");
            return;
        }

        while (true) {
            System.out.println("\n=== Imagens do Produto: " + product.getProduct() + " ===");
            System.out.println("1. Listar imagens");
            System.out.println("2. Adicionar imagem");
            System.out.println("3. Voltar");
            System.out.print("Escolha uma opção: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (option) {
                case 1:
                    listProductImages(productId);
                    break;
                case 2:
                    addProductImage(productId);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    public static void listProductImages(int productId) {
        List<ProductImage> images = productImageService.getProductImages(productId);
        Product product = productService.getProduct(productId);

        System.out.println("\n=== Listar imagens do Produto " + productId + " ===");

        if (images.isEmpty()) {
            System.out.println("Este produto não possui imagens cadastradas.");
        } else {
            System.out.printf("%-5s| %-20s | %-20s | %-10s |\n", "Id", "Nome Imagem", "diretório Destino", "Principal");
            System.out.println("--------------------------------------------------------------");

            for (ProductImage image : images) {
                System.out.printf("%-5d| %-20s | %-20s | %-10s |\n",
                        image.getId(),
                        image.getFileName(),
                        image.getDirectory(),
                        image.isPrimary() ? "sim" : "não");
            }
        }

        System.out.print("\nEntre com o id para remover a imagem, 0 para voltar e i para incluir => ");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("i")) {
            addProductImage(productId);
        } else if (input.equals("0")) {
            return;
        } else {
            try {
                int imageId = Integer.parseInt(input);
                System.out.print("Deseja remover esta imagem? (S/N): ");
                String confirm = scanner.nextLine();

                if (confirm.equalsIgnoreCase("S")) {
                    boolean success = productImageService.deleteProductImage(imageId);
                    if (success) {
                        System.out.println("Imagem removida com sucesso!");
                    } else {
                        System.out.println("Erro ao remover imagem.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("ID inválido!");
            }
        }
    }

    public static void addProductImage(int productId) {
        System.out.println("\n=== Incluir Imagem ===");

        System.out.print("Nome arquivo => ");
        String fileName = scanner.nextLine();

        System.out.print("Diretório Origem Imagem => ");
        String sourceDirectory = scanner.nextLine();

        System.out.print("Principal (S/N) => ");
        boolean isPrimary = scanner.nextLine().equalsIgnoreCase("S");

        System.out.println("-----------------------------------------------");
        System.out.println("Opções");
        System.out.println("1) Salvar e incluir +1 imagem de produto");
        System.out.println("2) Salvar e finalizar");
        System.out.println("3) Não salvar e finalizar");
        System.out.print("Entre com a opção (1,2,3) => ");

        int option = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        switch (option) {
            case 1:
                boolean success = productImageService.addProductImage(fileName, sourceDirectory, isPrimary, productId);
                if (success) {
                    System.out.println("Imagem adicionada com sucesso!");
                    addProductImage(productId); // Recursivamente chama para adicionar outra imagem
                } else {
                    System.out.println("Erro ao adicionar imagem!");
                }
                break;
            case 2:
                success = productImageService.addProductImage(fileName, sourceDirectory, isPrimary, productId);
                if (success) {
                    System.out.println("Imagem adicionada com sucesso!");
                } else {
                    System.out.println("Erro ao adicionar imagem!");
                }
                break;
            case 3:
                System.out.println("Operação cancelada.");
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }
}