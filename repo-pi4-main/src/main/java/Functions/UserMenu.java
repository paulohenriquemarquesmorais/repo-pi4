package Functions;

import model.User;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UserMenu {
    private static Scanner scanner = new Scanner(System.in);

    public static void showLoginMenu() {
        System.out.println("\n=== Menu de Login ===");
        System.out.println("1. Login");
        System.out.println("2. Sair");
        System.out.print("Escolha uma opção: ");

        try {
            int option = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (option) {
                case 1:
                    UsersFunctions.login(); // Chamada direta da classe UsersFunctions
                    break;
                case 2:
                    System.out.println("Encerrando o sistema...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Opção inválida! Digite um número válido.");
            scanner.nextLine(); // Limpar buffer para evitar loop infinito
            showLoginMenu();
        }
    }

    public static void showMenu(User loggedUser) {
        if (loggedUser == null) {
            System.out.println("Erro: Nenhum usuário está logado.");
            return;
        }

        if (loggedUser.isAdmin()) {
            showAdminMenu();
        } else {
            showUserMenu();
        }
    }

    public static void showAdminMenu() {
        while (true) {
            System.out.println("\n=== Menu de Administrador ===");
            System.out.println("1. Listar usuários");
            System.out.println("2. Cadastrar usuário");
            System.out.println("3. Excluir usuário");
            System.out.println("4. Alterar senha de um usuário");
            System.out.println("5. Alterar nível de acesso de um usuário");
            System.out.println("6. Ativar/Desativar usuário");
            System.out.println("7. Logout");
            System.out.println("8. Listar Produtos");
            System.out.print("Escolha uma opção: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer

                switch (option) {
                    case 1:
                        UsersFunctions.listUsers();
                        break;
                    case 2:
                        UsersFunctions.registerUser();
                        break;
                    case 3:
                        UsersFunctions.deleteUser();
                        break;
                    case 4:
                        UsersFunctions.changeUserPassword();
                        break;
                    case 5:
                        UsersFunctions.changeUserAccessLevel();
                        break;
                    case 6:
                        UsersFunctions.toggleUserStatus();
                        break;
                    case 7:
                        System.out.println("Saindo do menu de administrador...");
                        return; // Sai do loop e volta para a main()
                    case 8:
                        ProductsFunctions.listProducts();
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida! Por favor, insira um número.");
                scanner.nextLine(); // Limpar buffer em caso de erro de entrada
            }
        }
    }


    public static void showUserMenu() {
        System.out.println("\n=== Menu de Usuário ===");
        System.out.println("1. Logout");
        System.out.println("2. Listar produtos");
        System.out.print("Escolha uma opção: ");

        try {
            int option = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (option) {
                case 1:
                    UsersFunctions.logout(); // Chamada direta da classe UsersFunctions
                    break;
                case 2:
                    ProductsFunctions.listProducts();
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
        catch (InputMismatchException e) {
            System.out.println("Entrada inválida! Por favor, insira um número.");
            scanner.nextLine(); // Limpar buffer em caso de erro de entrada
            showUserMenu();
        }
    }
}
