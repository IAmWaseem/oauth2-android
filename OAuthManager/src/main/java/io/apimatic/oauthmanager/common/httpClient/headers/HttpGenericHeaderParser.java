package io.apimatic.oauthmanager.common.httpClient.headers;

import io.apimatic.oauthmanager.common.Holder;
import io.apimatic.oauthmanager.common.httpClient.HttpRuleParser;


final class HttpGenericHeaderParser extends BaseHeaderParser
{
    private interface ParsedValueLengthGetter
    {
        int apply(final String value, final int startIndex, final Holder<Object> parsedValue);
    }

    public static final HttpHeaderParser HostParser = new HttpGenericHeaderParser(false, new ParsedValueLengthGetter()
    {
        @Override
        public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
        {
            final Holder<Integer> index = new Holder<Integer>(startIndex);
            final Holder<String> host = new Holder<String>();
            final int hostLength = HttpRuleParser.getHostLength(value, index, false, host);
            parsedValue.value = host.value;
            return hostLength;
        }
    }, HttpHeaderUtils.getIgnoreCaseStringComparer());

    public static final HttpHeaderParser TokenListParser = new HttpGenericHeaderParser(true, new ParsedValueLengthGetter()
    {
        @Override
        public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
        {
            final Holder<Integer> index = new Holder<Integer>(startIndex);
            final int tokenLength = HttpRuleParser.getTokenLength(value, index);
            parsedValue.value = value.substring(startIndex, tokenLength);
            return tokenLength;
        }
    }, HttpHeaderUtils.getIgnoreCaseStringComparer());

    public static final HttpHeaderParser SingleValueEntityTagParser = new HttpGenericHeaderParser(false, new ParsedValueLengthGetter()
    {
        @Override
        public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
        {
            final Holder<Integer> index = new Holder<Integer>(startIndex);
            final Holder<EntityTagHeaderValue> entityTagHeaderValue = new Holder<EntityTagHeaderValue>();
            final int entityTagLength = EntityTagHeaderValue.getEntityTagLength(value, index, entityTagHeaderValue);
            if (entityTagHeaderValue.value == EntityTagHeaderValue.getAny()) return 0;
            parsedValue.value = entityTagHeaderValue.value;
            return entityTagLength;
        }
    });

    public static final HttpHeaderParser MultipleValueEntityTagParser = new HttpGenericHeaderParser(true, new ParsedValueLengthGetter()
    {
        @Override
        public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
        {
            final Holder<Integer> index = new Holder<Integer>(startIndex);
            final Holder<EntityTagHeaderValue> entityTagHeaderValue = new Holder<EntityTagHeaderValue>();
            final int entityTagLength = EntityTagHeaderValue.getEntityTagLength(value, index, entityTagHeaderValue);
            parsedValue.value = entityTagHeaderValue.value;
            return entityTagLength;
        }
    });

    private static class NameValueParser implements ParsedValueLengthGetter
    {
        @Override
        public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
        {
            final Holder<Integer> index = new Holder<Integer>(startIndex);
            final Holder<NameValueHeaderValue> nameValueHeaderValue = new Holder<NameValueHeaderValue>();
            final int nameValueLength = NameValueHeaderValue.getNameValueLength(value, index, nameValueHeaderValue, NameValueHeaderValue.class);
            parsedValue.value = nameValueHeaderValue.value;
            return nameValueLength;
        }
    }

    private static final NameValueParser DefaultNameValueParser = new NameValueParser();
    public static final HttpHeaderParser SingleValueNameValueParser = new HttpGenericHeaderParser(false, DefaultNameValueParser);
    public static final HttpHeaderParser MultipleValueNameValueParser = new HttpGenericHeaderParser(true, DefaultNameValueParser);

    public static final HttpHeaderParser ContentDispositionParser = new HttpGenericHeaderParser(false, new ParsedValueLengthGetter()
    {
        @Override
        public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
        {
            final Holder<Integer> index = new Holder<Integer>(startIndex);
            final Holder<ContentDispositionHeaderValue> contentDispositionHeaderValue = new Holder<ContentDispositionHeaderValue>();
            final int dispositionTypeLength = ContentDispositionHeaderValue.getDispositionTypeLength(value, index, contentDispositionHeaderValue);
            parsedValue.value = contentDispositionHeaderValue.value;
            return dispositionTypeLength;
        }
    });


    private static class ProductParser implements ParsedValueLengthGetter
    {
        @Override
        public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
        {
            final Holder<Integer> index = new Holder<Integer>(startIndex);
            final Holder<ProductHeaderValue> productHeaderValue = new Holder<ProductHeaderValue>();
            final int productHeaderLength = ProductHeaderValue.getProductLength(value, index, productHeaderValue);
            parsedValue.value = productHeaderValue.value;
            return productHeaderLength;
        }
    }

    private static final ProductParser DefaultProductParser = new ProductParser();
    public static final HttpHeaderParser SingleValueProductParser = new HttpGenericHeaderParser(false, DefaultProductParser);
    public static final HttpHeaderParser MultipleValueProductParser = new HttpGenericHeaderParser(true, DefaultProductParser);

    private static class WarningParser implements ParsedValueLengthGetter
    {
        @Override
        public int apply(final String value, final int startIndex, final Holder<Object> parsedValue)
        {
            final Holder<Integer> index = new Holder<Integer>(startIndex);
            final Holder<WarningHeaderValue> warningHeaderValue = new Holder<WarningHeaderValue>();
            final int dispositionTypeLength = WarningHeaderValue.getWarningLength(value, index, warningHeaderValue);
            parsedValue.value = warningHeaderValue.value;
            return dispositionTypeLength;
        }
    }

    private static final WarningParser DefaultWarningParser = new WarningParser();
    public static final HttpHeaderParser SingleValueWarningParser = new HttpGenericHeaderParser(false, DefaultWarningParser);
    public static final HttpHeaderParser MultipleValueWarningParser = new HttpGenericHeaderParser(true, DefaultWarningParser);

    private final ParsedValueLengthGetter _lengthGetter;
    private final EqualityComparer _comparer;

    public HttpGenericHeaderParser(final boolean supportsMultipleValues, ParsedValueLengthGetter lengthGetter)
    {
        this(supportsMultipleValues, lengthGetter, null);
    }

    public HttpGenericHeaderParser(final boolean supportsMultipleValues, ParsedValueLengthGetter lengthGetter, EqualityComparer comparer)
    {
        super(supportsMultipleValues);
        this._lengthGetter = lengthGetter;
        this._comparer = comparer;
    }

    @Override
    protected int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue)
    {
        return this._lengthGetter.apply(value, startIndex, parsedValue);
    }

    @Override
    public final EqualityComparer getComparer()
    {
        return this._comparer;
    }
}