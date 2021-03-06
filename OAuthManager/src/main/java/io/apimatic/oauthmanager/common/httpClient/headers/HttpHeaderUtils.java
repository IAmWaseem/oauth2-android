package io.apimatic.oauthmanager.common.httpClient.headers;

import io.apimatic.oauthmanager.common.Holder;
import io.apimatic.oauthmanager.common.httpClient.HttpParseResult;
import io.apimatic.oauthmanager.common.httpClient.HttpRuleParser;

import java.util.Collection;


final class HttpHeaderUtils
{
    public static HttpHeaderValueCollection.Validator<String> getTokenValidator()
    {
        return _tokenValidator;
    }
    private static final HttpHeaderValueCollection.Validator<String> _tokenValidator = new HttpHeaderValueCollection.Validator<String>()
    {
        @Override
        public void valid(final HttpHeaderValueCollection<String> self, final String item)
        {
            validateToken(self, item);
        }
    };

    static final TransferEncodingHeaderValue TRANSFER_ENCODING_CHUNKCED = new TransferEncodingHeaderValue("chunked");

    private static final HttpHeaderParser.EqualityComparer _ignoreCaseStringComparer = new HttpHeaderParser.EqualityComparer()
    {
        @Override
        public boolean equals(final Object x, final Object y)
        {
            if (x == y) return true;
            if (!(x instanceof String) || !(y instanceof String)) return false;

            final String xs = (String)x;
            final String ys = (String)y;
            return xs.equalsIgnoreCase(ys);
        }
    };

    public static HttpHeaderParser.EqualityComparer getIgnoreCaseStringComparer()
    {
        return _ignoreCaseStringComparer;
    }

    static void checkValidToken(final String value)
    {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException();

        final Holder<Integer> index = new Holder<Integer>(0);
        if (HttpRuleParser.getTokenLength(value, index) != value.length()) throw new IllegalArgumentException();
    }

    static void checkValidComment(final String value)
    {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException();

        final Holder<Integer> length = new Holder<Integer>(0);
        if (HttpRuleParser.getCommentLength(value, 0, length) != HttpParseResult.Parsed || length.value != value.length()) throw new IllegalArgumentException();
    }

    static void checkValidQuotedString(final String value)
    {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException();

        final Holder<Integer> length = new Holder<Integer>(0);
        if (HttpRuleParser.getQuotedStringLength(value, 0, length) != HttpParseResult.Parsed || length.value != value.length()) throw new IllegalArgumentException();
    }

    static boolean tryParseInt16(final String value, final Holder<Short> parsedValue)
    {
        parsedValue.value = 0;
        try
        {
            parsedValue.value = Short.parseShort(value);
        }
        catch (final NumberFormatException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static boolean tryParseInt32(final String value, final Holder<Integer> parsedValue)
    {
        parsedValue.value = 0;
        try
        {
            parsedValue.value = Integer.parseInt(value);
        }
        catch (final NumberFormatException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static boolean tryParseInt64(final String value, final Holder<Long> parsedValue)
    {
        parsedValue.value = 0L;
        try
        {
            parsedValue.value = Long.parseLong(value);
        }
        catch (final NumberFormatException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void validateToken(final HttpHeaderValueCollection<String> target, final String value)
    {
        checkValidToken(value);
    }

    public static <T> boolean equalsCollection(final Collection<T> x, final Collection<T> y)
    {
        if (x == null) return y == null || y.size() == 0;
        if (y == null) return x.size() == 0;
        if (x.size() != y.size()) return false;
        if (x.size() == 0) return true;

        for (final T xItem : x)
        {
            boolean flag = false;
            for (final T yItem : y)
            {
                if (xItem.equals(yItem))
                {
                    flag = true;
                    break;
                }
            }
            if (!flag) return false;
        }
        return true;
    }

    public static <T> boolean equalsCollection(final Collection<T> x, final Collection<T> y, final HttpHeaderParser.EqualityComparer comparer)
    {
        if (comparer == null) throw new IllegalArgumentException();
        if (x == null) return y == null || y.size() == 0;
        if (y == null) return x.size() == 0;
        if (x.size() != y.size()) return false;
        if (x.size() == 0) return true;

        for (final T xItem : x)
        {
            boolean flag = false;
            for (final T yItem : y)
            {
                if (comparer.equals(xItem, yItem))
                {
                    flag = true;
                    break;
                }
            }
            if (!flag) return false;
        }
        return true;
    }
}