package io.apimatic.oauthmanager.utils;

import java.util.List;

/**
 * Created by Waseem on 6/13/2016.
 */
public class ListUtils {
     public static <T> void Concat(T source, T destination) {
         for(T item : (List<T>)destination) {
             ((List<T>)source).add(item);
         }
     }
}
