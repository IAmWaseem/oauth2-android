package io.apimatic.oauthmanager.common.tokens;

import java.util.AbstractMap;
import java.util.ArrayList;

/**
 * Created by Waseem on 6/9/2016.
 */
public class AuthToken extends Token {
    public String Code;
    public AuthToken() {}
    public AuthToken(String code, ArrayList<AbstractMap.Entry<String, String>> extraData) {
        super(code, extraData);
    }
}
