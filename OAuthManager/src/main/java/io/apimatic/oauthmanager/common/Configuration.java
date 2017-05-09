package io.apimatic.oauthmanager.common;

import android.content.Context;

/**
 * Created by Waseem on 6/15/2016.
 */
public class Configuration {
    private static Context _context;
    public static void Initialize(Context context) {
        _context = context;
    }

    public static Context getContext() {
        return _context;
    }

}
