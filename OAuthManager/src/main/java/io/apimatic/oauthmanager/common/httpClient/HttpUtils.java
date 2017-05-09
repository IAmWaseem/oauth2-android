package io.apimatic.oauthmanager.common.httpClient;

import io.apimatic.oauthmanager.common.Holder;

import java.net.URL;


/**
 * Created by Waseem on 6/13/2016.
 */
public final class HttpUtils
{
    public static int getNextNonEmptyOrWhitespaceIndex(final String input, final int startIndex, final boolean skipEmptyValues, final Holder<Boolean> seperatorFound)
    {
        seperatorFound.value = false;

        final int length = input.length();
        int idx = startIndex + HttpRuleParser.getWhitespaceLength(input, startIndex);
        if (idx == length || input.charAt(idx) != ',') return idx;

        seperatorFound.value = true;
        ++idx;
        idx += HttpRuleParser.getWhitespaceLength(input, idx);
        if (skipEmptyValues)
        {
            while (idx < length && input.charAt(idx) == ',')
            {
                ++idx;
                idx += HttpRuleParser.getWhitespaceLength(input, idx);
            }
        }
        return idx;
    }

    public static boolean isHttpUrl(final URL url)
    {
        final String protocol = url.getProtocol();
        return "http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol);
    }
}