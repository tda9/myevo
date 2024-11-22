package com.da.iam.utils;

public class UserUtils {
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\+?[0-9]{11}$");
    }
    public static boolean isValidDOB(String dob) {
        return dob.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }
}
