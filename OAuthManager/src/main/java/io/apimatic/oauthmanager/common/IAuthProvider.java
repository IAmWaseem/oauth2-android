package io.apimatic.oauthmanager.common;


import io.apimatic.oauthmanager.common.httpClient.HttpRequestMessage;

/**
 * Created by Waseem on 6/13/2016.
 */
public interface IAuthProvider {
    boolean AppendCredentials(HttpRequestMessage request) throws Exception;
}
