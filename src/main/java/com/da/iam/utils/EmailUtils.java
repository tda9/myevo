package com.da.iam.utils;

public class EmailUtils {


    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final String EMAIL_SUBJECT = "Reset your password";
    public static final String EMAIL_MESSAGE = "To reset your password, click the link below:\n";
    public static final String EMAIL_FROM = "";
    public static final String PASSWORD_RESET_LINK = "http://localhost:8080/reset-password?token=";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    public static boolean isValidEmail(String email) {
        return email.matches(EMAIL_PATTERN);
    }

}
