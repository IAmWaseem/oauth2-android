package io.apimatic.oauthmanager.core.Methods.TwoLegged;


import android.net.Uri;

import io.apimatic.oauthmanager.common.IUserAuthorizationViewer;
import io.apimatic.oauthmanager.common.IUserConsentHandler;
import io.apimatic.oauthmanager.common.OAuthConstants;
import io.apimatic.oauthmanager.common.TokenResponse;
import io.apimatic.oauthmanager.common.tokens.AccessToken;
import io.apimatic.oauthmanager.core.OAuth2Base;
import io.apimatic.oauthmanager.utils.StringUtils;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Waseem on 6/13/2016.
 */
public class OAuth2BrowserBasedFlow extends OAuth2Base implements IUserConsentHandler {

    public OAuth2BrowserBasedFlow(String clientId, String redirectUrl, String scope, String authorizationUrl) {
        super(clientId, redirectUrl, scope, authorizationUrl);
    }

    public Uri GetUserTokenUrl() {
        //initialize the underlying OAuthorizer
        InitAuthorizer();

        super.oAuthState = oAuthState.AUTH_TOKEN_WAIT;
        String authorizeUrlResponse = Authorizer.BuildAuthorizeUrl(AuthorizationUrl, OAuthConstants.RESPONSE_TYPE_TOKEN);
        return Uri.parse(authorizeUrlResponse);
    }

    public void InvokeUserAuthorization(IUserAuthorizationViewer viewer) {
        //initialize the underlying OAuthorizer
        InitAuthorizer();

        super.oAuthState = oAuthState.AUTH_TOKEN_WAIT;
        String authorizeUrlResponse = Authorizer.BuildAuthorizeUrl(AuthorizationUrl, OAuthConstants.RESPONSE_TYPE_TOKEN);
        viewer.setAuthController(this);
        viewer.setAuthorizeUri(Uri.parse(authorizeUrlResponse));
    }

    @Override
    public boolean IsCallback(Uri currentUrl) {
        if (RedirectUrl.equals(OAuthConstants.OUT_OF_BOUNDS))
            return false;

        Uri callBackUrl = Uri.parse(RedirectUrl);

        String value1 = currentUrl.getScheme() + currentUrl.getHost() + currentUrl.getPort() + currentUrl.getPath();
        String value2 = callBackUrl.getScheme() + callBackUrl.getHost() + callBackUrl.getPort() + callBackUrl.getPath();

        return value1.equals(value2);
    }

    @Override
    public CompletableFuture<Boolean> ProcessUserAuthorizationAsync(Uri currentUrl) throws Exception {

        final Uri authorizedUrl = StringUtils.ProcessIEUrlErrors(currentUrl);

        CompletableFuture<Boolean> resultToReturn = CompletableFuture.supplyAsync(new CompletableFuture.Generator<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    TokenResponse<AccessToken> result = Authorizer.GetAccessTokenFromResponse(authorizedUrl);
                    if (result != null) {
                        oAuthState = oAuthState.SUCCEEDED;
                        accessToken = result.token;
                        return true;
                    }
                    oAuthState = oAuthState.FAILED;
                    return false;
                } catch (Exception e) {
                    oAuthState = oAuthState.FAILED;
                    try {
                        throw new Exception("");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                return false;
            }
        });
        return resultToReturn;
    }
}