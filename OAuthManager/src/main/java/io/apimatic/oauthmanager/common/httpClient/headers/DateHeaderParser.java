package io.apimatic.oauthmanager.common.httpClient.headers;

/**
 * Created by Waseem on 6/13/2016.
 */
import io.apimatic.oauthmanager.common.Holder;
import io.apimatic.oauthmanager.common.httpClient.HttpRuleParser;

import java.util.Date;


final class DateHeaderParser extends HttpHeaderParser
{
    public static final DateHeaderParser Instance = new DateHeaderParser();

    private DateHeaderParser()
    {
        super(false);
    }

    @Override
    public String toString(final Object value)
    {
        return HttpRuleParser.dateToString((Date)value);
    }

    @Override
    public boolean tryParseValue(String input, final Object storeValue, final Holder<Integer> index, final Holder<Object> parsedValue)
    {
        if (input == null || input.isEmpty()) return false;

        final int length = input.length();
        if (index.value == length) return false;
        if (index.value != 0) input = input.substring(index.value);

        final Holder<Date> result = new Holder<Date>();
        if (!HttpRuleParser.tryStringToDate(input, result)) return false;

        index.value = length;
        parsedValue.value = result.value;
        return true;
    }
}