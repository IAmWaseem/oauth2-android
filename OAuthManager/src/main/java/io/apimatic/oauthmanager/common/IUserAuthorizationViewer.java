package io.apimatic.oauthmanager.common;

import android.net.Uri;

/**
 * Created by Waseem on 6/13/2016.
 */
public interface IUserAuthorizationViewer {
    Uri getAuthorizeUrl();

    void setAuthorizeUri(Uri uri);

    IUserConsentHandler getAuthController();

    void setAuthController(IUserConsentHandler consentHandler);
}
