package Functions;

import java.util.Scanner;
import service.UserService;

public class UserMenu {
    private static Scanner scanner = new Scanner(System.in);

    public static void showUserMenu() {
        UsersFunctions.listUsers();
    }
}
