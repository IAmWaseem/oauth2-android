package io.apimatic.oauthmanager.core.Methods.TwoLegged;

import android.net.Uri;

import io.apimatic.oauthmanager.common.OAuthConstants;
import io.apimatic.oauthmanager.common.TokenResponse;
import io.apimatic.oauthmanager.core.OAuth2Base;
import io.apimatic.oauthmanager.common.tokens.AccessToken;

import java.util.AbstractMap;
import java.util.ArrayList;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Waseem on 6/13/2016.
 */
public class OAuth2ResourceOwnerPassword extends OAuth2Base {
    private String userName;
    private String password;
    private String clientSecret;

    public OAuth2ResourceOwnerPassword(String clientId, String clientSecret, String redirectUri, String scope,
                                       String authorizationUrl) {
        super(clientId, redirectUri, scope, authorizationUrl);
        this.clientSecret = clientSecret;
    }

    public OAuth2ResourceOwnerPassword(String clientId, String clientSecret, String redirectUrl, String scope, String authorizationUrl, String userName, String password) {
        super(clientId, redirectUrl, scope, authorizationUrl);
        this.userName = userName;
        this.password = password;
        this.clientSecret = clientSecret;
    }


    public CompletableFuture<Boolean> InvokeUserAuthorization(String userName, String password) {
        if (userName != "" || this.userName == null) {
            this.userName = userName;
        }
        if (password != "" || this.password == null) {
            this.password = password;
        }

        //initialize the underlying OAuthorizer
        InitAuthorizer(clientSecret);

        super.oAuthState = oAuthState.INITIALIZED;
        ArrayList<AbstractMap.Entry<String, String>> parameters = new ArrayList<>();
        parameters.add(new AbstractMap.SimpleEntry<String, String>(OAuthConstants.USERNAME, this.userName));
        parameters.add(new AbstractMap.SimpleEntry<String, String>(OAuthConstants.PASSWORD, this.password));
        parameters.add(new AbstractMap.SimpleEntry<String, String>(OAuthConstants.CLIENT_SECRET, this.clientSecret));

        final String accessTokenUrl = Authorizer.BuildAuthorizeUrl(AuthorizationUrl, OAuthConstants.GRANT_TYPE_PASSWORD, parameters);
        Uri authUri = Uri.parse(accessTokenUrl);
        CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(new CompletableFuture.Generator<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    TokenResponse<AccessToken> result = Authorizer.GetAccessTokenAsync(accessTokenUrl).get();

                    if (result != null) {
                        oAuthState = oAuthState.SUCCEEDED;
                        accessToken = result.token;
                        return true;
                    } else {
                        oAuthState = oAuthState.FAILED;
                        return false;
                    }
                } catch (Exception ex) {
                    oAuthState = oAuthState.FAILED;

                }
                return false;
            }
        });
        oAuthState = oAuthState.ACCESS_TOKEN_WAIT;

        return result;
    }

    public CompletableFuture<Boolean> InvokeUserAuthorization(String userName) {
        return InvokeUserAuthorization(userName, "");
    }

    public CompletableFuture<Boolean> InvokeUserAuthorization() {
        return InvokeUserAuthorization("");
    }
}