package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncryptor {

    // Gera um hash da senha com um salt gerado aleatoriamente
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    // Verifica se uma senha em texto puro corresponde ao hash armazenado
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}