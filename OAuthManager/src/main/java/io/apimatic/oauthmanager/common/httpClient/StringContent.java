package io.apimatic.oauthmanager.common.httpClient;

import java.nio.charset.Charset;

/**
 * Created by Waseem on 6/13/2016.
 */
public final class StringContent extends ByteArrayContent
{
    public StringContent(final String content)
    {
        this(content, null);
    }

    public StringContent(final String content, final Charset charset)
    {
        this(content, charset, null);
    }

    public StringContent(final String content, Charset charset, String mediaType)
    {
        super(getByteArrayContent(content, charset));

        if (mediaType == null || mediaType.isEmpty()) mediaType = "text/plain";
        this.setContentType(mediaType, charset);
    }

    private static byte[] getByteArrayContent(final String content, Charset charset)
    {
        if (content == null) throw new IllegalArgumentException();
        if (charset == null) charset = DEFAULT_CHARSET;

        return content.getBytes(charset);
    }
}