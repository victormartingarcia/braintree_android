package com.braintreecv1payments.api.internal;

public class ClassHelper {

    public static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
