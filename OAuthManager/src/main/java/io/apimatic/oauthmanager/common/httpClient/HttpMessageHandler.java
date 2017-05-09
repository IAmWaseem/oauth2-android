package io.apimatic.oauthmanager.common.httpClient;

import java.io.Closeable;
import java.io.IOException;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Waseem on 6/13/2016.
 */
public abstract class HttpMessageHandler implements Closeable
{
    @Override
    public final void close() throws IOException
    {
        this.close(true);
    }

    protected void close(final boolean closing)
    {
    }

    abstract CompletableFuture<HttpResponseMessage> sendAsync(HttpRequestMessage request);
}