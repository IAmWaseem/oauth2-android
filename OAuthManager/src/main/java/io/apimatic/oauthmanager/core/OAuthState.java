package io.apimatic.oauthmanager.core;

/**
 * Created by Waseem on 6/13/2016.
 */
public enum OAuthState
{
    INITIALIZED,
    UNINITIALIZED,
    REQUEST_TOKEN_WAIT,
    AUTH_TOKEN_WAIT,
    ACCESS_TOKEN_WAIT,
    SUCCEEDED,
    FAILED,
    REFRESH_WAIT
}