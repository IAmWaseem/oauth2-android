package io.apimatic.oauthmanager.core;

import io.apimatic.oauthmanager.common.IAuthProvider;
import io.apimatic.oauthmanager.common.OAuthConstants;
import io.apimatic.oauthmanager.common.httpClient.HttpRequestMessage;
import io.apimatic.oauthmanager.common.tokens.AccessToken;
import io.apimatic.oauthmanager.common.tokens.AuthToken;

import java.io.InvalidObjectException;
import java.util.AbstractMap;
import java.util.ArrayList;

/**
 * Created by Waseem on 6/13/2016.
 */
public class OAuth2Base implements IAuthProvider {

    protected OAuth2Authorizer Authorizer;
    protected AuthToken authToken;

    public AccessToken accessToken;

    protected String ClientId;
    protected String RedirectUrl;
    protected String AuthorizationUrl;
    protected String Scope;

    public OAuthState oAuthState;

    public void RestoreAccessToken(String accessToken, String expiresIn, String refreshToken) {
        ArrayList<AbstractMap.Entry<String, String>> parameters = new ArrayList<>();

        if(expiresIn != null && expiresIn.trim() != "") {
            parameters.add(new AbstractMap.SimpleEntry<String, String>(OAuthConstants.EXPIRES_IN, expiresIn));
        }

        if(refreshToken != null && refreshToken.trim() != "") {
            parameters.add(new AbstractMap.SimpleEntry<String, String>(OAuthConstants.REFRESH_TOKEN, refreshToken));
        }

        this.accessToken = new AccessToken(accessToken, parameters);
    }

    public OAuth2Base(String clientId, String redirectUrl, String scope, String authorizationUrl) {
        ClientId = clientId;
        RedirectUrl = redirectUrl;
        Scope = scope;
        AuthorizationUrl = authorizationUrl;
        oAuthState = oAuthState.INITIALIZED;
    }

    protected void InitAuthorizer() {
        Authorizer = new OAuth2Authorizer(ClientId, RedirectUrl, Scope);
    }

    protected void InitAuthorizer(String clientSecret) {
        Authorizer = new OAuth2Authorizer(ClientId, clientSecret, RedirectUrl, Scope);
    }

    @Override
    public boolean AppendCredentials(HttpRequestMessage request) throws Exception {
        if(oAuthState == oAuthState.FAILED) {
            throw new InvalidObjectException("The OAuth Process has failed to authorize this request.");
        }
        if(oAuthState != oAuthState.SUCCEEDED) {
            throw new InvalidObjectException("The OAuth process must finish before this request can be made.");
        }
        String headerVal = accessToken.Code;
        request.getHeaders().tryAddWithoutValidation(OAuthConstants.OAUTH_HEADER, new String[] { String.format(OAuthConstants.OAUTH_HEADER_VALUE_FORMAT, headerVal) });
        return true;
    }
}
