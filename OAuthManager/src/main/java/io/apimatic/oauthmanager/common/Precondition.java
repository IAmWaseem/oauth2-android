package io.apimatic.oauthmanager.common;

/**
 * Created by Waseem on 6/13/2016.
 */
public class Precondition {
    public static void NotNull(Object obj, String parameterName, String message) {
        if(obj == null)
            throw new IllegalArgumentException(parameterName, new Throwable(message));
    }

    public static void NotNull(Object obj, String parameterName) {
        NotNull(obj, parameterName, "");
    }

    public static void NotNull(Object obj) {
        NotNull(obj, "");
    }

    public static void NotNullOrEmpty(String value, String parameterName, String message)
    {
        if(value == null || value == "") {
            throw new IllegalArgumentException(parameterName, new Throwable(message));
        }
    }

    public static void NotNullOrEmpty(String value, String parameterName) {
        NotNullOrEmpty(value, parameterName, "");
    }

    public static void NotNullOrEmpty(String value) {
        NotNullOrEmpty(value, "");
    }

    public static void Requires(boolean condition, String parameterName, String message)
    {
        if(!condition)
            throw new IllegalArgumentException(parameterName, new Throwable(message));
    }

    public static void Requires(boolean condition, String parameterName) {
        Requires(condition, parameterName, "");
    }

    public static void Requires(boolean condition) {
        Requires(condition, "");
    }


}
