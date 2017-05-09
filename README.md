# OAuthManager-Android

  Usage

    Configuration.Initialize(this);
    OAuth2WebServerFlow oAuth = new OAuth2WebServerFlow(GoogleClientId, ClientSecret, GoogleCallbackUrl, 
                Scope, AuthorizationUrl, AccessTokenUrl);
    OAuthUiBroker.ShowOAuthView(oAuth);
