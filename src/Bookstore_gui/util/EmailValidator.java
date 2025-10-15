/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Bookstore_gui.util;

/**
 *
 * @author julia
 */
import java.util.regex.Pattern;

public class EmailValidator {
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private EmailValidator() {}
    public static boolean isValid(String email) {
        return email != null && EMAIL.matcher(email).matches();
    }
}
