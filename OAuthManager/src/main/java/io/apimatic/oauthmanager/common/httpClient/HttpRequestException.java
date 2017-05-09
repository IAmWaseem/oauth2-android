package io.apimatic.oauthmanager.common.httpClient;

/**
 * Created by Waseem on 6/13/2016.
 */
public final class HttpRequestException extends Exception
{
    public HttpRequestException()
    {
        super();
    }

    public HttpRequestException(final String message)
    {
        super(message);
    }

    public HttpRequestException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}