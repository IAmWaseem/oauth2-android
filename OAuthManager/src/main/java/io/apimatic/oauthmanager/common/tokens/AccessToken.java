package io.apimatic.oauthmanager.common.tokens;

        import io.apimatic.oauthmanager.common.OAuthConstants;

        import java.util.AbstractMap;
        import java.util.ArrayList;


/**
 * Created by Waseem on 6/9/2016.
 */
public class AccessToken extends Token {
    public String Code;

    public AccessToken() { }
    public AccessToken(String code, ArrayList<AbstractMap.Entry<String, String>> extraData) {
        super(code, extraData);
    }

    public String ExpiresIn() {
        for(int i = 0; i < ExtraData.size(); i++) {
            if(ExtraData.get(i).getKey() == OAuthConstants.EXPIRES_IN) {
                return ExtraData.get(i).getValue();
            }
        }
        return "";
    }

    public String RefreshToken() {
        for(int i = 0; i < ExtraData.size(); i++) {
            if(ExtraData.get(i).getKey() == OAuthConstants.REFRESH_TOKEN) {
                return ExtraData.get(i).getValue();
            }
        }
        return "";
    }
}
