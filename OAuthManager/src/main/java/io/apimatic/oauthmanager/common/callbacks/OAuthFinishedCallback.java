package io.apimatic.oauthmanager.common.callbacks;

/**
 * Created by Waseem on 6/20/2016.
 */
public interface OAuthFinishedCallback {
    void onOAuthSucceeded(String accessToken, String expiresIn, String refreshToken);
    void onOAuthFailed(Exception ex);
}
