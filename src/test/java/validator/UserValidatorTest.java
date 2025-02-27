package validator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class UserValidatorTest {

    @Test
    void testValidEmail() {
        assertTrue(UserValidator.isValidEmail("user@example.com"));
        assertTrue(UserValidator.isValidEmail("user.name@example.co.uk"));
        assertTrue(UserValidator.isValidEmail("user+tag@example.com"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalidemail", "user@", "@example.com", "user@.com", "user@example."})
    void testInvalidEmail(String email) {
        assertFalse(UserValidator.isValidEmail(email));
    }

    @Test
    void testValidUsername() {
        assertTrue(UserValidator.isValidUsername("username"));
        assertTrue(UserValidator.isValidUsername("user123"));
        assertTrue(UserValidator.isValidUsername("user_name"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"us", "user name", "user@name", "verylongusernamethatismorethan20characters"})
    void testInvalidUsername(String username) {
        assertFalse(UserValidator.isValidUsername(username));
    }

    @Test
    void testValidPassword() {
        assertTrue(UserValidator.isValidPassword("Password123"));
        assertTrue(UserValidator.isValidPassword("P@ssw0rd"));
        assertTrue(UserValidator.isValidPassword("SecurePassword123!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"short", "nouppercase123", "NOLOWERCASE123", "NoNumbers", "12345678"})
    void testInvalidPassword(String password) {
        assertFalse(UserValidator.isValidPassword(password));
    }
}