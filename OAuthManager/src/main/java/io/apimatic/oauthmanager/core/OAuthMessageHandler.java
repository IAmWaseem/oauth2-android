package io.apimatic.oauthmanager.core;

import io.apimatic.oauthmanager.common.OAuthConstants;
import io.apimatic.oauthmanager.common.httpClient.DelegatingHandler;
import io.apimatic.oauthmanager.common.httpClient.FormUrlEncodedContent;
import io.apimatic.oauthmanager.common.httpClient.HttpClientHandler;
import io.apimatic.oauthmanager.common.httpClient.HttpMethod;
import io.apimatic.oauthmanager.common.httpClient.HttpRequestMessage;
import io.apimatic.oauthmanager.common.httpClient.HttpResponseMessage;
import io.apimatic.oauthmanager.utils.ListUtils;
import io.apimatic.oauthmanager.utils.OAuthUtils;
import io.apimatic.oauthmanager.utils.StringUtils;

import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Waseem on 6/13/2016.
 */
public class OAuthMessageHandler extends DelegatingHandler {
    private String clientId;
    private String redirectUrl;
    private ArrayList<AbstractMap.Entry<String, String>> parameters;

    public OAuthMessageHandler(String clientId, String redirectUrl,
                               ArrayList<AbstractMap.Entry<String, String>> optionalParameters)
    {
        super(new HttpClientHandler());
        this.clientId = clientId;
        this.redirectUrl = redirectUrl != null ? redirectUrl : OAuthConstants.LOCALHOST;
        this.parameters = optionalParameters != null ? optionalParameters : new ArrayList<AbstractMap.Entry<String, String>>();
    }

    public OAuthMessageHandler(String clientId, String redirectUrl) {
        this(clientId, redirectUrl, null);
    }

    @Override
    public CompletableFuture<HttpResponseMessage> sendAsync(final HttpRequestMessage request) {
        ArrayList<AbstractMap.Entry<String, String>> parametersToSend = parameters;
        if(request.getMethod() == HttpMethod.Post) {
            if(request.getContent() instanceof FormUrlEncodedContent) {
                try {
                    String extraParams = request.getContent().readAsStringAsync().get();
                    List<AbstractMap.SimpleEntry<String, String>> parsedParams = OAuthUtils.ParseQueryString(extraParams);
                    ListUtils.Concat(parametersToSend, parsedParams);
                } catch(Exception ex) {
                    ex.printStackTrace();

                }
            }
        }

        ArrayList<AbstractMap.Entry<String, String>> authParams = OAuthUtils.BuildBasicParams(clientId, parametersToSend);

        if(request.getMethod() == HttpMethod.Post) {
            try {
                request.setContent(new FormUrlEncodedContent(authParams.iterator()));
            } catch(Exception ex) {
                ex.printStackTrace();

            }
        }
        else if(request.getMethod() == HttpMethod.Get) {
            String queryData = "";
            int count = 0;
            while(authParams.size() > 0) {
                AbstractMap.Entry<String, String> param = authParams.get(count);
                queryData += param.getKey() + "=" + param.getValue() + "&";
                count++;
            }
            queryData = StringUtils.TrimEnd(queryData, "&");

            String newQuery = request.getRequestUrl().getQuery();

            if(newQuery == null || newQuery.trim() == "") {
                newQuery = "?" + queryData;
            }
            else {
                newQuery += "&" + queryData;
            }

            try {
                request.setRequestUrl(new URL(request.getRequestUrl().toString() + newQuery));
            } catch(Exception ex) {
                ex.printStackTrace();

            }
        }
        return super.sendAsync(request);
    }
}
