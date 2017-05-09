package io.apimatic.oauthmanager.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.apimatic.oauthmanager.common.IUserAuthorizationViewer;
import io.apimatic.oauthmanager.common.IUserConsentHandler;

/**
 * Created by Waseem on 6/15/2016.
 */
public class WebAuthWebView extends WebView implements IUserAuthorizationViewer {

    public interface OAuthResultCallback {
        void onOAuthSucceeded(Uri url);
        void onOAuthFailed();
    }


    private OAuthResultCallback callback;


    public WebAuthWebView(Context context, final OAuthResultCallback callback) {
        super(context);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.callback = callback;
        this.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(AuthController.IsCallback(Uri.parse(url))){
                    try {
                        callback.onOAuthSucceeded(Uri.parse(url));
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private Uri AuthorizeUrl;
    private IUserConsentHandler AuthController;

    @Override
    public Uri getAuthorizeUrl() {
        return AuthorizeUrl;
    }

    @Override
    public void setAuthorizeUri(Uri uri) {
        this.AuthorizeUrl = uri;
    }

    @Override
    public IUserConsentHandler getAuthController() {
        return AuthController;
    }

    @Override
    public void setAuthController(IUserConsentHandler consentHandler) {
        this.AuthController = consentHandler;
    }
}
