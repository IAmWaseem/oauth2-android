package io.apimatic.oauthmanager.common.httpClient;

import java.util.ArrayList;

/**
 * Created by Waseem on 6/13/2016.
 */
public final class GenericHelpers
{
    public static boolean isSafetyGenericsList(final Object source, final Class genericClass)
    {
        if (!(source instanceof ArrayList<?>))
        {
            return false;
        }

        boolean safe = true;
        final ArrayList rawValueList = (ArrayList)source;
        for (final Object rawValueItem : rawValueList)
        {
            if (!(genericClass.isAssignableFrom(rawValueItem.getClass())))
            {
                safe = false;
                break;
            }
        }
        return safe;
    }

    public static <T> T getDefault(final Class<T> genericClass)
    {
        if (genericClass == Boolean.class) return (T)new Boolean(false);
        if (genericClass == Integer.class) return (T)new Integer(0);
        if (genericClass == Long.class) return (T)new Long(0);
        return null;
    }
}