package org.example;

import model.User;


public class Main {
    private static User loggedUser = null;


    public static void main(String[] args) {
        System.out.println("Sistema de Gerenciamento de Usuários");

        while (true) {
            if (loggedUser == null) {
                Functions.UserMenu.showLoginMenu();
            } else if (loggedUser.isAdmin()) {
                Functions.UserMenu.showAdminMenu();
            } else {
                Functions.UserMenu.showUserMenu();
            }
        }
    }
}