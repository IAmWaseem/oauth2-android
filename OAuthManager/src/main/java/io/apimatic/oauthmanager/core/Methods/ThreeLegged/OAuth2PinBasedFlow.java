package io.apimatic.oauthmanager.core.Methods.ThreeLegged;

import android.net.Uri;

import io.apimatic.oauthmanager.common.OAuthConstants;
import io.apimatic.oauthmanager.common.TokenResponse;
import io.apimatic.oauthmanager.common.tokens.AccessToken;
import io.apimatic.oauthmanager.common.tokens.AuthToken;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Waseem on 6/13/2016.
 */
public class OAuth2PinBasedFlow extends OAuth2ThreeLeggedBase {
    public OAuth2PinBasedFlow(String clientId, String clientSecret, String redirectUrl, String scope, String authorizationUrl, String accessTokenUrl) {
        super(clientId, clientSecret, redirectUrl, scope, authorizationUrl, accessTokenUrl);
    }

    public Uri GetUserTokenUrl() {
        //initialize the underlying OAuthorizer
        InitAuthorizer(clientSecret);

        super.oAuthState = oAuthState.AUTH_TOKEN_WAIT;
        String authorizeUrlResponse = Authorizer.BuildAuthorizeUrl(AuthorizationUrl, OAuthConstants.RESPONSE_TYPE_CODE);
        return Uri.parse(authorizeUrlResponse);
    }

    public CompletableFuture<Boolean> ProcessUserAuthorizationAsync(String verifier) {
        oAuthState = oAuthState.ACCESS_TOKEN_WAIT;
        authToken = new AuthToken(verifier, null);
        CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(new CompletableFuture.Generator<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    TokenResponse<AccessToken> result = Authorizer.GetAccessTokenAsync(accessTokenUrl, authToken, OAuthConstants.GRANT_TYPE_AUTH_CODE).get();

                    if (result != null) {
                        oAuthState = oAuthState.SUCCEEDED;
                        accessToken = result.token;
                        return true;
                    } else {
                        oAuthState = oAuthState.FAILED;
                        return false;
                    }
                } catch(Exception ex) {

                }
                return false;
            }
        });
        return result;
    }
}