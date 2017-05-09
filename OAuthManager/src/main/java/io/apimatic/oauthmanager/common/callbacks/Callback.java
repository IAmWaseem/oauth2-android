package io.apimatic.oauthmanager.common.callbacks;

/**
 * Created by Waseem on 6/13/2016.
 */
public interface Callback<T> {

    void onSuccess(T result);
    void onFailure(Throwable error);
}
