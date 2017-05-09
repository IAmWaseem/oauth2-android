package io.apimatic.oauthmanager.common;

import io.apimatic.oauthmanager.common.tokens.TokenBase;

/**
 * Created by Waseem on 6/13/2016.
 */
public class TokenResponse<T> extends TokenBase {
    public T token;

    public TokenResponse(T token) {
        Precondition.NotNull(token, "token");
        this.token = token;
    }
}
