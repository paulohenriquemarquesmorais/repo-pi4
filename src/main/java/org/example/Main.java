import java.util.Scanner;
import service.UserService;
import model.User;
import java.util.List;
import validator.UserValidator;

public class Main {
    private static UserService userService = new UserService();
    private static Scanner scanner = new Scanner(System.in);
    private static User loggedUser = null;

    public static void main(String[] args) {
        System.out.println("Sistema de Gerenciamento de Usuários");

        while (true) {
            if (loggedUser == null) {
                showLoginMenu();
            } else if (loggedUser.isAdmin()) {
                showAdminMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n=== Menu de Login ===");
        System.out.println("1. Login");
        System.out.println("2. Sair");
        System.out.print("Escolha uma opção: ");

        int option = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        switch (option) {
            case 1:
                login();
                break;
            case 2:
                System.out.println("Encerrando o sistema...");
                System.exit(0);
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    private static void showAdminMenu() {
        System.out.println("\n=== Menu de Administrador ===");
        System.out.println("1. Listar usuários");
        System.out.println("2. Cadastrar usuário");
        System.out.println("3. Excluir usuário");
        System.out.println("4. Alterar senha de um usuário");
        System.out.println("5. Alterar nível de acesso de um usuário");
        System.out.println("6. Ativar/Desativar usuário");
        System.out.println("7. Logout");
        System.out.print("Escolha uma opção: ");

        int option = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        switch (option) {
            case 1:
                listUsers();
                break;
            case 2:
                registerUser();
                break;
            case 3:
                deleteUser();
                break;
            case 4:
                changeUserPassword();
                break;
            case 5:
                changeUserAccessLevel();
                break;
            case 6:
                toggleUserStatus();
                break;
            case 7:
                logout();
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    private static void showUserMenu() {
        System.out.println("\n=== Menu de Usuário ===");
        System.out.println("1. Ver meus dados");
        System.out.println("2. Alterar minha senha");
        System.out.println("3. Logout");
        System.out.print("Escolha uma opção: ");

        int option = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        switch (option) {
            case 1:
                viewMyData();
                break;
            case 2:
                changeMyPassword();
                break;
            case 3:
                logout();
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    private static void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        loggedUser = userService.authenticate(username, password);

        if (loggedUser != null) {
            System.out.println("Login realizado com sucesso! Bem-vindo, " + loggedUser.getName());
        } else {
            System.out.println("Credenciais inválidas ou usuário inativo!");
        }
    }

    private static void logout() {
        loggedUser = null;
        System.out.println("Logout realizado com sucesso!");
    }

    private static void listUsers() {
        List<User> users = userService.getAllUsers();

        System.out.println("\n=== Lista de Usuários ===");
        for (User user : users) {
            System.out.println("ID: " + user.getId() +
                    " | Nome: " + user.getName() +
                    " | Username: " + user.getUsername() +
                    " | Email: " + user.getEmail() +
                    " | Admin: " + (user.isAdmin() ? "Sim" : "Não") +
                    " | Status: " + (user.isActive() ? "Ativo" : "Inativo"));
        }
    }

    private static void registerUser() {
        User newUser = new User();

        System.out.println("\n=== Cadastro de Usuário ===");
        System.out.print("Nome: ");
        newUser.setName(scanner.nextLine());

        String username;
        do {
            System.out.print("Username (3-20 caracteres, apenas letras, números e _): ");
            username = scanner.nextLine();
            if (!UserValidator.isValidUsername(username)) {
                System.out.println("Username inválido!");
            }
        } while (!UserValidator.isValidUsername(username));
        newUser.setUsername(username);

        String email;
        do {
            System.out.print("Email: ");
            email = scanner.nextLine();
            if (!UserValidator.isValidEmail(email)) {
                System.out.println("Email inválido!");
            }
        } while (!UserValidator.isValidEmail(email));
        newUser.setEmail(email);

        String password;
        do {
            System.out.print("Senha (mínimo 8 caracteres, letras maiúsculas, minúsculas e números): ");
            password = scanner.nextLine();
            if (!UserValidator.isValidPassword(password)) {
                System.out.println("Senha inválida!");
            }
        } while (!UserValidator.isValidPassword(password));
        newUser.setPassword(password);

        System.out.print("Administrador (S/N): ");
        String isAdmin = scanner.nextLine();
        newUser.setAdmin(isAdmin.equalsIgnoreCase("S"));

        newUser.setActive(true); // Novos usuários são ativos por padrão

        boolean success = userService.registerUser(newUser);

        if (success) {
            System.out.println("Usuário cadastrado com sucesso!");
        } else {
            System.out.println("Erro ao cadastrar usuário. Username ou email já existem.");
        }
    }

    private static void deleteUser() {
        System.out.print("Digite o ID do usuário a ser excluído: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        // Verificar se o usuário existe
        User user = userService.getUser(userId);
        if (user == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        // Exibir dados do usuário
        System.out.println("\n=== Dados do Usuário a ser Excluído ===");
        System.out.println("ID: " + user.getId());
        System.out.println("Nome: " + user.getName());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());

        System.out.print("Confirma a exclusão deste usuário? (S/N): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("S")) {
            boolean success = userService.deleteUser(userId);

            if (success) {
                System.out.println("Usuário excluído com sucesso!");
            } else {
                System.out.println("Erro ao excluir usuário. ID não encontrado.");
            }
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }

    private static void changeUserPassword() {
        System.out.print("Digite o ID do usuário: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        // Verificar se o usuário existe
        User user = userService.getUser(userId);
        if (user == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        // Exibir dados do usuário
        System.out.println("\n=== Dados do Usuário ===");
        System.out.println("ID: " + user.getId());
        System.out.println("Nome: " + user.getName());
        System.out.println("Username: " + user.getUsername());

        String newPassword;
        do {
            System.out.print("Digite a nova senha (mínimo 8 caracteres, letras maiúsculas, minúsculas e números): ");
            newPassword = scanner.nextLine();
            if (!UserValidator.isValidPassword(newPassword)) {
                System.out.println("Senha inválida!");
            }
        } while (!UserValidator.isValidPassword(newPassword));

        boolean success = userService.changeUserPassword(userId, newPassword);

        if (success) {
            System.out.println("Senha alterada com sucesso!");
        } else {
            System.out.println("Erro ao alterar senha. ID não encontrado.");
        }
    }

    private static void changeUserAccessLevel() {
        System.out.print("Digite o ID do usuário: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        // Verificar se o usuário existe
        User user = userService.getUser(userId);
        if (user == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        // Exibir dados do usuário
        System.out.println("\n=== Dados do Usuário ===");
        System.out.println("ID: " + user.getId());
        System.out.println("Nome: " + user.getName());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Status atual de administrador: " + (user.isAdmin() ? "SIM" : "NÃO"));

        System.out.print("Tornar administrador (S/N): ");
        String isAdmin = scanner.nextLine();
        boolean adminStatus = isAdmin.equalsIgnoreCase("S");

        // Se o status atual já for o desejado
        if (user.isAdmin() == adminStatus) {
            System.out.println("O usuário já possui este nível de acesso!");
            return;
        }

        boolean success = userService.changeUserAccessLevel(userId, adminStatus);

        if (success) {
            System.out.println("Nível de acesso alterado com sucesso!");
        } else {
            System.out.println("Erro ao alterar nível de acesso. ID não encontrado.");
        }
    }

    private static void toggleUserStatus() {
        System.out.print("Digite o ID do usuário: ");
        int userId = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        // Buscar o usuário para verificar o status atual
        User user = userService.getUser(userId);

        if (user == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        // Exibir dados do usuário
        System.out.println("\n=== Dados do Usuário ===");
        System.out.println("ID: " + user.getId());
        System.out.println("Nome: " + user.getName());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Status atual: " + (user.isActive() ? "ATIVO" : "INATIVO"));

        // Mostrar opção contrária do status atual
        System.out.print("Deseja " + (user.isActive() ? "DESATIVAR" : "ATIVAR") + " este usuário? (S/N): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("S")) {
            boolean success = userService.toggleUserActiveStatus(userId);

            if (success) {
                System.out.println("Status do usuário alterado com sucesso para " +
                        (!user.isActive() ? "ATIVO" : "INATIVO") + "!");
            } else {
                System.out.println("Erro ao alterar status do usuário!");
            }
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private static void viewMyData() {
        System.out.println("\n=== Meus Dados ===");
        System.out.println("ID: " + loggedUser.getId());
        System.out.println("Nome: " + loggedUser.getName());
        System.out.println("Username: " + loggedUser.getUsername());
        System.out.println("Email: " + loggedUser.getEmail());
        System.out.println("Administrador: " + (loggedUser.isAdmin() ? "Sim" : "Não"));
    }

    private static void changeMyPassword() {
        System.out.print("Digite sua senha atual: ");
        String currentPassword = scanner.nextLine();

        if (!loggedUser.getPassword().equals(currentPassword)) {
            System.out.println("Senha atual incorreta!");
            return;
        }

        String newPassword;
        do {
            System.out.print("Digite a nova senha (mínimo 8 caracteres, letras maiúsculas, minúsculas e números): ");
            newPassword = scanner.nextLine();
            if (!UserValidator.isValidPassword(newPassword)) {
                System.out.println("Senha inválida!");
            }
        } while (!UserValidator.isValidPassword(newPassword));

        boolean success = userService.changeUserPassword(loggedUser.getId(), newPassword);

        if (success) {
            System.out.println("Sua senha foi alterada com sucesso!");
            loggedUser.setPassword(newPassword);
        } else {
            System.out.println("Erro ao alterar sua senha.");
        }
    }

    // Métodos adicionados para facilitar testes
    public static void setUserService(UserService service) {
        userService = service;
    }

    public static void setLoggedUser(User user) {
        loggedUser = user;
    }
}