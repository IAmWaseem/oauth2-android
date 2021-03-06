package io.apimatic.oauthmanager.common.httpClient.headers;


import io.apimatic.oauthmanager.common.Holder;

final class MediaTypeWithQualityHeaderParser extends BaseHeaderParser
{
    public static final MediaTypeWithQualityHeaderParser SingleValueParser = new MediaTypeWithQualityHeaderParser(false);
    public static final MediaTypeWithQualityHeaderParser MultipleValuesParser = new MediaTypeWithQualityHeaderParser(true);

    private MediaTypeWithQualityHeaderParser(final boolean supportsMultipleValues)
    {
        super(supportsMultipleValues);
    }

    @Override
    protected int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue)
    {
        final Holder<Integer> index = new Holder<Integer>(startIndex);
        final Holder<MediaTypeWithQualityHeaderValue> mediaTypeHeaderValue = new Holder<MediaTypeWithQualityHeaderValue>();
        final int mediaTypeLength = MediaTypeHeaderValueBase.getMediaTypeLength(value, index, mediaTypeHeaderValue, MediaTypeWithQualityHeaderValue.class);
        parsedValue.value = mediaTypeHeaderValue.value;
        return mediaTypeLength;
    }
}