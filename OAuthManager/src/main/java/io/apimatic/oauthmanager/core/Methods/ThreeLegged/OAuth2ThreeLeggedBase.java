package io.apimatic.oauthmanager.core.Methods.ThreeLegged;

import android.net.Uri;

import io.apimatic.oauthmanager.common.OAuthConstants;
import io.apimatic.oauthmanager.common.TokenResponse;
import io.apimatic.oauthmanager.common.tokens.AccessToken;
import io.apimatic.oauthmanager.core.OAuth2Base;

import java.util.AbstractMap;
import java.util.ArrayList;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Waseem on 6/13/2016.
 */
public class OAuth2ThreeLeggedBase extends OAuth2Base {
    protected String accessTokenUrl;
    protected String clientSecret;

    public OAuth2ThreeLeggedBase(String clientId, String clientSecret,
                                 String redirectUrl, String scope, String authorizationUrl, String accessTokenUrl) {
        super(clientId, redirectUrl, scope, authorizationUrl);
        this.accessTokenUrl = accessTokenUrl;
        this.clientSecret = clientSecret;
    }

    public CompletableFuture<Boolean> RefreshAccessToken() throws Exception {
        //initialize the underlying OAuthorizer
        if (Authorizer == null)
            InitAuthorizer(clientSecret);

        if (oAuthState != oAuthState.SUCCEEDED) {
            throw new Exception("The request must be authorized before refresh.");
        }

        if (accessToken == null) {
            throw new Exception("The access token has not previously been acquired.");
        }

        if (accessToken.RefreshToken() == null || accessToken.RefreshToken().trim() == "") {
            throw new Exception("Refresh token was not found.");
        }

        oAuthState = oAuthState.REFRESH_WAIT;

        ArrayList<AbstractMap.Entry<String, String>> parameters = new ArrayList<>();
        parameters.add(new AbstractMap.SimpleEntry<String, String>(OAuthConstants.REFRESH_TOKEN, this.accessToken.RefreshToken()));
        parameters.add(new AbstractMap.SimpleEntry<String, String>(OAuthConstants.CLIENT_SECRET, this.clientSecret));

        final String accessTokenUrl = Authorizer.BuildAuthorizeUrl(AuthorizationUrl, OAuthConstants.GRANT_TYPE_REFRESH_TOKEN, parameters);
        Uri authUri = Uri.parse(accessTokenUrl);

        oAuthState = oAuthState.ACCESS_TOKEN_WAIT;
        CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(new CompletableFuture.Generator<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    TokenResponse<AccessToken> result = Authorizer.GetAccessTokenAsync(accessTokenUrl).get();

                    if (result != null) {
                        oAuthState = oAuthState.SUCCEEDED;
                        AccessToken token = result.token;
                        OAuth2ThreeLeggedBase.this.RestoreAccessToken(token.Code, token.ExpiresIn(), token.RefreshToken());

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
        return result;
    }


}