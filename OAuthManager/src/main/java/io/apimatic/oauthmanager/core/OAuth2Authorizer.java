package io.apimatic.oauthmanager.core;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.apimatic.oauthmanager.common.OAuthConstants;
import io.apimatic.oauthmanager.common.Precondition;
import io.apimatic.oauthmanager.common.TokenResponse;
import io.apimatic.oauthmanager.common.httpClient.FormUrlEncodedContent;
import io.apimatic.oauthmanager.common.httpClient.HttpClient;
import io.apimatic.oauthmanager.common.httpClient.HttpContent;
import io.apimatic.oauthmanager.common.httpClient.HttpRequestException;
import io.apimatic.oauthmanager.common.httpClient.HttpResponseMessage;
import io.apimatic.oauthmanager.common.httpClient.HttpStatusCode;
import io.apimatic.oauthmanager.common.tokens.AccessToken;
import io.apimatic.oauthmanager.common.tokens.AuthToken;
import io.apimatic.oauthmanager.common.tokens.Token;
import io.apimatic.oauthmanager.utils.StringUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Waseem on 6/13/2016.
 */
public class OAuth2Authorizer {
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String scope;

    private OAuth2Authorizer(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = "";
        this.redirectUrl = OAuthConstants.LOCALHOST;
    }

    public OAuth2Authorizer(String consumerKey, String consumerSecret, String redirectUrl, String scope) {
        this(consumerKey, consumerSecret);
        this.redirectUrl = redirectUrl;
        this.scope = scope;
    }

    public OAuth2Authorizer(String consumerKey, String redirectUrl, String scope) {
        this.clientId = consumerKey;
        this.clientSecret = null;
        this.redirectUrl = redirectUrl;
        this.scope = scope;
    }

    private static <T> TokenResponse<T> ExtractTokenAndExtraData(String tokenName,
                                                                 TokenResponseCallback<T> tokenFactory, String tokenBase)
    {
        String[] splitted = tokenBase.split("&");
        ArrayList<AbstractMap.Entry<String, String>> entries = new ArrayList<>();
        for(String item : splitted) {
            String[] subItem = item.split("=");
            entries.add(new AbstractMap.SimpleEntry<>(subItem[0], subItem[1]));
        }
        AbstractMap.Entry<String, String> tokenItem = null;
        for(AbstractMap.Entry<String, String> item : entries) {
            if(item.getKey().equals(tokenName)) {
                tokenItem = item;
            }
        }

        if(tokenItem != null) {
            String code = StringUtils.UrlDecode(tokenItem.getValue());
            entries.remove(tokenItem);
            T token = tokenFactory.onTokenReceived(code, entries);
            return new TokenResponse(token);
        }
        throw new IllegalStateException();
    }

    private static <T> TokenResponse<T> ExtractTokenAndExtraDataJson(String tokenName,
    TokenResponseCallback<T> tokenFactory, String tokenBase) {
        //TODO: Might Cause Issues
        Map<String, String> results = new Gson().fromJson(tokenBase, new TypeToken<HashMap<String, String>>(){}.getType());
        String accessToken = null;
        ArrayList<AbstractMap.Entry<String, String>> entries = new ArrayList<>();

        for(String item : results.keySet()) {
            if(item.equals(tokenName)) {
                accessToken = results.get(item);
                continue;
            }
            entries.add(new AbstractMap.SimpleEntry<String, String>(item, results.get(item)));
        }
        T token = tokenFactory.onTokenReceived(accessToken, entries);
        return new TokenResponse(token);
    }

    private <T> TokenResponse<T> GetTokenResponseFromQuery(
    Uri url, String tokenName, TokenResponseCallback<T> tokenFactory)
    {
        String tokenBase = url.getQuery();
        if(tokenBase.startsWith("?"))
            tokenBase = tokenBase.substring(1);

        return ExtractTokenAndExtraData(tokenName, tokenFactory, tokenBase);
    }

    private <T> TokenResponse<T> GetTokenResponseFromFragment(
    Uri url, String tokenName, TokenResponseCallback<T> tokenFactory) {
        String tokenBase = url.getFragment();
        if(tokenBase.startsWith("#"))
            tokenBase = tokenBase.substring(1);

        return ExtractTokenAndExtraData(tokenName, tokenFactory, tokenBase);
    }

    public TokenResponse<AccessToken> GetAccessTokenFromResponse(Uri url)
    {
        TokenResponse<AccessToken> tokenResponse = GetTokenResponseFromFragment
                (url, OAuthConstants.ACCESS_TOKEN, new TokenResponseCallback<AccessToken>() {
                    @Override
                    public AccessToken onTokenReceived(String code, ArrayList<AbstractMap.Entry<String, String>> extraData) {
                        return new AccessToken(code, extraData);
                    }
                });
        return tokenResponse;
    }

    public TokenResponse<AuthToken> GetAuthTokenFromResponse(Uri url)
    {
        TokenResponse<AuthToken> tokenResponse = GetTokenResponseFromQuery
                (url, OAuthConstants.RESPONSE_TYPE_CODE, new TokenResponseCallback<AuthToken>() {
                    @Override
                    public AuthToken onTokenReceived(String code, ArrayList<Map.Entry<String, String>> extraData) {
                        return new AuthToken(code, extraData);
                    }
                });
        return tokenResponse;
    }

    public String BuildAuthorizeUrl(String authUrl,
                                    String responseType, ArrayList<AbstractMap.Entry<String, String>> optionalParameters)
    {
        Precondition.NotNull(authUrl, "authUrl");

        ArrayList<AbstractMap.Entry<String, String>> parameters = new ArrayList<>();

        parameters.add(new AbstractMap.SimpleEntry<>(OAuthConstants.RESPONSE_TYPE, responseType));
        parameters.add(new AbstractMap.SimpleEntry<>(OAuthConstants.CLIENT_ID, clientId));
        parameters.add(new AbstractMap.SimpleEntry<>(OAuthConstants.REDIRECT_URI, redirectUrl));
        parameters.add(new AbstractMap.SimpleEntry<>(OAuthConstants.SCOPE, scope));

        if (optionalParameters == null) optionalParameters = new ArrayList<>();
        ArrayList<AbstractMap.Entry<String, String>> stringParameter = optionalParameters;
        for(AbstractMap.Entry<String, String> item : stringParameter) {
            if(item.getKey().toLowerCase() == OAuthConstants.REALM)
                stringParameter.remove(item);
        }

        stringParameter.addAll(parameters);


        String finalParametersString = "";

        for(AbstractMap.Entry<String, String> item : stringParameter) {
            finalParametersString += StringUtils.UrlEncode(item.getKey()) + "=" + StringUtils.UrlEncode(item.getValue()) + "&";
        }

        finalParametersString = StringUtils.TrimEnd(finalParametersString, "&");
        return authUrl + "?" + finalParametersString;
    }

    public String BuildAuthorizeUrl(String authUrl, String responseType) {
        return BuildAuthorizeUrl(authUrl, responseType, null);
    }

    private <T> CompletableFuture<TokenResponse<T>> GetTokenResponseAsync(String url, OAuthMessageHandler handler,
                                                                          HttpContent postValue, final TokenResponseCallback<T> tokenFactory)
    {
        HttpClient client = new HttpClient(handler);
        CompletableFuture<TokenResponse<T>> result = null;
        try {
           HttpResponseMessage response  = client.postAsync(url, postValue == null ? new FormUrlEncodedContent(new HashMap<String, String>()) : postValue).get();
            final String tokenBase = response.getContent().readAsStringAsync().get();

            if(response.getStatusCode() != HttpStatusCode.OK) {
                throw new HttpRequestException(response.getStatusCode() + ":" + tokenBase);
            }

            if(!tokenBase.contains(OAuthConstants.ACCESS_TOKEN)) return null;

            result = CompletableFuture.supplyAsync(new CompletableFuture.Generator<TokenResponse<T>>() {
                @Override
                public TokenResponse<T> get() {
                    return StringUtils.IsJson(tokenBase) ? ExtractTokenAndExtraDataJson(OAuthConstants.ACCESS_TOKEN, tokenFactory, tokenBase) : ExtractTokenAndExtraData(OAuthConstants.ACCESS_TOKEN, tokenFactory, tokenBase);
                }
            });
        } catch(Exception ex) {
            Log.d("Exception", ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    public CompletableFuture<TokenResponse<AccessToken>> GetAccessTokenAsync(String accessTokenUrl,
                                                                      ArrayList<AbstractMap.Entry<String, String>> parameters, HttpContent postValue)
    {
        Precondition.NotNull(accessTokenUrl, "accessTokenUrl");

        if (parameters == null) parameters = new ArrayList<>();
        OAuthMessageHandler handler = new OAuthMessageHandler(clientId, redirectUrl, parameters);

        return GetTokenResponseAsync(accessTokenUrl, handler, postValue, new TokenResponseCallback<AccessToken>() {
            @Override
            public AccessToken onTokenReceived(String code, ArrayList<Map.Entry<String, String>> extraData) {
                return new AccessToken(code, extraData);
            }
        });
    }

    public CompletableFuture<TokenResponse<AccessToken>> GetAccessTokenAsync(String accessTokenUrl, ArrayList<AbstractMap.Entry<String, String>> parameters) {
        return GetAccessTokenAsync(accessTokenUrl, parameters, null);
    }
    public CompletableFuture<TokenResponse<AccessToken>> GetAccessTokenAsync(String accessTokenUrl) {
        return GetAccessTokenAsync(accessTokenUrl, null);
    }

    public CompletableFuture<TokenResponse<AccessToken>> GetAccessTokenAsync(String accessTokenUrl, AuthToken authToken,
                                                                      String grantType, ArrayList<AbstractMap.Entry<String, String>> parameters, HttpContent postValue)
    {
        Precondition.NotNull(accessTokenUrl, "accessTokenUrl");
        Precondition.NotNull(authToken, "authToken");
        Precondition.NotNull(grantType, "grantType");
        Precondition.NotNull(clientId, "clientId");
        Precondition.NotNull(clientSecret, "clientSecret");

        ArrayList<AbstractMap.Entry<String, String>> sendParameters = new ArrayList<>();
        sendParameters.add(new AbstractMap.SimpleEntry<>(OAuthConstants.CODE, ((Token)authToken).Code));
        sendParameters.add(new AbstractMap.SimpleEntry<>(OAuthConstants.CLIENT_SECRET, clientSecret));
        sendParameters.add(new AbstractMap.SimpleEntry<>(OAuthConstants.REDIRECT_URI, redirectUrl));
        sendParameters.add(new AbstractMap.SimpleEntry<>(OAuthConstants.GRANT_TYPE, grantType));
        sendParameters.add(new AbstractMap.SimpleEntry<>(OAuthConstants.SCOPE, scope));


        if (parameters == null) parameters = new ArrayList<>();
        for(AbstractMap.Entry<String, String> item : sendParameters) {
            parameters.add(item);
        }
        OAuthMessageHandler handler = new OAuthMessageHandler(clientId, redirectUrl, parameters);

        return GetTokenResponseAsync(accessTokenUrl, handler, postValue, new TokenResponseCallback<AccessToken>() {
        @Override
        public AccessToken onTokenReceived(String code, ArrayList<Map.Entry<String, String>> extraData) {
            return new AccessToken(code, extraData);
        }
    });
    }

    public CompletableFuture<TokenResponse<AccessToken>> GetAccessTokenAsync(String accessTokenUrl, AuthToken authToken,
                                                                             String grantType, ArrayList<AbstractMap.Entry<String, String>> parameters) {
        return GetAccessTokenAsync(accessTokenUrl, authToken, grantType, parameters, null);
    }

    public CompletableFuture<TokenResponse<AccessToken>> GetAccessTokenAsync(String accessTokenUrl, AuthToken authToken,
                                                                             String grantType) {
        return GetAccessTokenAsync(accessTokenUrl, authToken, grantType, null);
    }


    private interface TokenResponseCallback<T> {
        T onTokenReceived(String code, ArrayList<AbstractMap.Entry<String, String>> extraData);
    }





}
