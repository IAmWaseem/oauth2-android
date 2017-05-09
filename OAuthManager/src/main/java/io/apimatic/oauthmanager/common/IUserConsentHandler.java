package io.apimatic.oauthmanager.common;

import android.net.Uri;

import jersey.repackaged.jsr166e.CompletableFuture;


/**
 * Created by Waseem on 6/13/2016.
 */
public interface IUserConsentHandler {
    boolean IsCallback(Uri currentUrl);
    CompletableFuture<Boolean> ProcessUserAuthorizationAsync(Uri currentUrl) throws Exception;
}
