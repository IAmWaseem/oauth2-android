package io.apimatic.oauthmanager.common.httpClient.headers;



import io.apimatic.oauthmanager.common.Holder;

final class MediaTypeHeaderParser extends BaseHeaderParser
{
    public static final MediaTypeHeaderParser SingleValueParser = new MediaTypeHeaderParser(false);

    private MediaTypeHeaderParser(final boolean supportsMultipleValues)
    {
        super(supportsMultipleValues);
    }

    @Override
    protected int getParsedValueLength(final String value, final int startIndex, final Object storeValue, final Holder<Object> parsedValue)
    {
        final Holder<Integer> index = new Holder<Integer>(startIndex);
        final Holder<io.apimatic.oauthmanager.common.httpClient.headers.MediaTypeHeaderValue> mediaTypeHeaderValue = new Holder<MediaTypeHeaderValue>();
        final int mediaTypeLength = MediaTypeHeaderValueBase.getMediaTypeLength(value, index, mediaTypeHeaderValue, MediaTypeHeaderValue.class);
        parsedValue.value = mediaTypeHeaderValue.value;
        return mediaTypeLength;
    }
}