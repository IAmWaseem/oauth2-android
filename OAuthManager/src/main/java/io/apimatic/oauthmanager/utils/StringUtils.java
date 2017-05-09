package io.apimatic.oauthmanager.utils;

import android.net.Uri;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Waseem on 6/13/2016.
 */
public class StringUtils {
    public static String TrimEnd(String input, String charsToTrim)
    {
        return input.replaceAll("[" + charsToTrim + "]+$", "");
    }

    public static String TrimStart(String input, String charsToTrim)
    {
        return input.replaceAll("^[" + charsToTrim + "]+", "");
    }

    public static String Trim(String input, String charsToTrim)
    {
        return input.replaceAll("^[" + charsToTrim + "]+|[" + charsToTrim + "]+$", "");
    }

    public static String UrlDecode(String stringToDecode) {
        try {
            return URLDecoder.decode(stringToDecode.replace("+", " "), "UTF-8")
                    .replace("%21", "!")
                    .replace("%2A", "*")
                    .replace("%27", "'")
                    .replace("%28", "(")
                    .replace("%29", ")");
        } catch(Exception ex) {
            ex.printStackTrace();

        }
        return "";
    }

    public static String UrlEncode(String stringToEncode) {
        try {
            return URLEncoder.encode(stringToEncode, "UTF-8")
                    .replace("!", "%21")
                    .replace("*", "%2A")
                    .replace("'", "%27")
                    .replace("(", "%28")
                    .replace(")", "%29");
        } catch(Exception ex) {
            ex.printStackTrace();

        }
        return "";
    }

    public static boolean IsJson(String data)
    {
        Pattern pattern = Pattern.compile("\\{(((\\s)*(\\\"|\\\')[^,:\\\'\\\"]+(\\\"|\\\')(\\s)*\\:(\\s)*(\\\"|\\\')?[^,:\\\'\\\"]+(\\\"|\\\')?(\\s)*),)*((\\s)*(\\\"|\\\')[^,:\\\'\\\"]+(\\\"|\\\')(\\s)*\\:(\\s)*(\\\"|\\\')?[^,:\\\'\\\"]+(\\\"|\\\')?(\\s)*)\\}");
        Matcher m = pattern.matcher(data);
        return m.matches();
    }

    public static Uri ProcessIEUrlErrors(Uri current)
    {
        if (current.getAuthority().toLowerCase().equals("ieframe.dll"))
        {
            return Uri.parse(current.getFragment().substring(1));
        }
        return current;
    }
}
