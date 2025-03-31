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
                    UsersFunctions.login();
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
            scanner.nextLine();
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
            System.out.println("\n\t\tTela principal de backoffice\n");
            System.out.println("\t1. Listar Produto");
            System.out.println("\t2. Listar Usuário");
            System.out.println("\t3. Sair\n");
            System.out.print("\tEntre com a opção (1, 2 ou 3) => ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    ProductsFunctions.listProducts();
                    break;
                case "2":
                    UsersFunctions.listUsers();
                    break;
                case "3":
                    System.out.println("Encerrando o sistema...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("\n\tOpção inválida. Tente novamente.");
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
            scanner.nextLine();

            switch (option) {
                case 1:
                    UsersFunctions.logout();
                    break;
                case 2:
                    ProductsFunctions.listProducts();
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida! Por favor, insira um número.");
            scanner.nextLine();
            showUserMenu();
        }
    }


}
