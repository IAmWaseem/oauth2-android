package io.apimatic.oauthmanager.common;

import android.app.ActionBar;
import android.app.Activity;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.apimatic.oauthmanager.common.callbacks.OAuthFinishedCallback;
import io.apimatic.oauthmanager.common.tokens.Token;
import io.apimatic.oauthmanager.controls.WebAuthWebView;
import io.apimatic.oauthmanager.core.Methods.ThreeLegged.OAuth2WebServerFlow;


/**
 * Created by Waseem on 6/15/2016.
 */
public class OAuthUiBroker {
    private static WebAuthWebView oAuthView;
    private static ViewGroup backedUpView;
    public static void ShowOAuthView(final OAuth2WebServerFlow oAuth, final OAuthFinishedCallback callback) {
        oAuthView = new WebAuthWebView(Configuration.getContext(), new WebAuthWebView.OAuthResultCallback() {
            @Override
            public void onOAuthSucceeded(Uri url) {
                try {
                    oAuth.ProcessUserAuthorizationAsync(url).get();
                    Activity am = (Activity)Configuration.getContext();
                    ((ViewGroup)am.getWindow().getDecorView().findViewById(android.R.id.content)).removeView(oAuthView);
                    am.setContentView(backedUpView);
                    callback.onOAuthSucceeded(((Token)oAuth.accessToken).Code, oAuth.accessToken.ExpiresIn(), oAuth.accessToken.RefreshToken());
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
            @Override
            public void onOAuthFailed() {
                callback.onOAuthFailed(new Exception("Failed to Authenticate"));
            }
        });
        oAuth.InvokeUserAuthorization(oAuthView);
        oAuthView.loadUrl(oAuthView.getAuthorizeUrl().toString());
        Activity am = (Activity)Configuration.getContext();
        backedUpView = (ViewGroup)((ViewGroup)am.getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
        RelativeLayout rl = new RelativeLayout(Configuration.getContext());
        rl.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rl.addView(oAuthView);
        am.setContentView(rl);
    }
}
