package io.apimatic.oauthmanager.utils;

import io.apimatic.oauthmanager.common.OAuthConstants;
import io.apimatic.oauthmanager.common.Precondition;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Waseem on 6/13/2016.
 */
public class OAuthUtils {
    public static List<AbstractMap.SimpleEntry<String, String>> ParseQueryString(String query)
    {
        String[] data = StringUtils.TrimStart(query, "?").split("&");
        ArrayList<AbstractMap.SimpleEntry<String, String>> pairsToReturn = new ArrayList<>();
        for (String line: data) {
            if(line != "") {
                String[] keyValue = line.split("=");
                pairsToReturn.add(new AbstractMap.SimpleEntry<>(StringUtils.UrlDecode(keyValue[0]), StringUtils.UrlDecode(keyValue[1])));
            }
        }
        return pairsToReturn;
    }

    public static ArrayList<AbstractMap.Entry<String, String>> BuildBasicParams(String clientId, ArrayList<AbstractMap.Entry<String, String>> passedParameters) {
        Precondition.NotNullOrEmpty(clientId, "clientId");

        ArrayList<AbstractMap.Entry<String, String>> parameters = new ArrayList<>();
        parameters.add(new AbstractMap.SimpleEntry<>(OAuthConstants.CLIENT_ID, clientId));

        if(passedParameters != null) {
            parameters.addAll(passedParameters);
        }

        return parameters;
    }
}
