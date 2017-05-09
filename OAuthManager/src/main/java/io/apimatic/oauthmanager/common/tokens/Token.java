package io.apimatic.oauthmanager.common.tokens;

import java.util.AbstractMap;
import java.util.ArrayList;

/**
 * Created by Waseem on 6/9/2016.
 */
public abstract class Token extends TokenBase {
    public String Code;

    public Token() {}
    public Token(String code, ArrayList<AbstractMap.Entry<String, String>> extraData)  {
        Code = code;
        ExtraData = extraData;
    }
}
