package io.apimatic.oauthmanager.common;

import java.io.Serializable;

/**
 * Created by Waseem on 6/13/2016.
 */
public final class Holder<T> implements Serializable {
    public T value;

    public Holder() {
    }

    public Holder(T value) {
        this.value = value;
    }
}
