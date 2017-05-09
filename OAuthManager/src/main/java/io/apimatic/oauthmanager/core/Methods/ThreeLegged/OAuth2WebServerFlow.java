package io.apimatic.oauthmanager.core.Methods.ThreeLegged;

import android.net.Uri;

import io.apimatic.oauthmanager.common.IUserAuthorizationViewer;
import io.apimatic.oauthmanager.common.IUserConsentHandler;
import io.apimatic.oauthmanager.common.OAuthConstants;
import io.apimatic.oauthmanager.common.TokenResponse;
import io.apimatic.oauthmanager.core.OAuthState;
import io.apimatic.oauthmanager.utils.StringUtils;
import io.apimatic.oauthmanager.common.tokens.AccessToken;
import io.apimatic.oauthmanager.common.tokens.AuthToken;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Waseem on 6/13/2016.
 */
public class OAuth2WebServerFlow extends OAuth2ThreeLeggedBase implements IUserConsentHandler {
    public OAuth2WebServerFlow(String clientId, String clientSecret, String redirectUrl, String scope, String authorizationUrl, String accessTokenUrl) {
        super(clientId, clientSecret, redirectUrl, scope, authorizationUrl, accessTokenUrl);
    }

    public void InvokeUserAuthorization(IUserAuthorizationViewer viewer) {
        //initialize the underlying OAuthorizer
        InitAuthorizer(clientSecret);

        super.oAuthState = oAuthState.AUTH_TOKEN_WAIT;
        String authorizeUrlResponse = Authorizer.BuildAuthorizeUrl(AuthorizationUrl, OAuthConstants.RESPONSE_TYPE_CODE);
        viewer.setAuthController(this);
        viewer.setAuthorizeUri(Uri.parse(authorizeUrlResponse));
    }

    public boolean IsCallback(Uri currentUrl) {
        String processedUrl = StringUtils.ProcessIEUrlErrors(currentUrl).toString();
        if (RedirectUrl.equals(OAuthConstants.OUT_OF_BOUNDS))
            return false;

        String value1 = processedUrl.toString().toLowerCase();
        String value2 = RedirectUrl.toString().toLowerCase();
        return value1.startsWith(value2);
    }

    public CompletableFuture<Boolean> ProcessUserAuthorizationAsync(Uri currentUrl) throws Exception {
        Uri authorizedUrl = StringUtils.ProcessIEUrlErrors(currentUrl);
        oAuthState = oAuthState.AUTH_TOKEN_WAIT;
        TokenResponse<AuthToken> output = Authorizer.GetAuthTokenFromResponse(authorizedUrl);
        authToken = output.token;
        authToken.Code = output.token.Code;
        oAuthState = OAuthState.ACCESS_TOKEN_WAIT;

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
                } catch (Exception ex) {
                    ex.printStackTrace();
                    oAuthState = oAuthState.FAILED;
                }
                return false;
            }
        });
        return result;
    }
}