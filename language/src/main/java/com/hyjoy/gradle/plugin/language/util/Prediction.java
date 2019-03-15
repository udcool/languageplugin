package com.hyjoy.gradle.plugin.language.util;

import java.util.Collection;
import java.util.Map;

/**
 * Created by hyjoy on 2019/3/15.
 */
public final class Prediction {

    public static void checkNotNull(String content, String msg) {
        if (content == null || content.length() == 0) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void checkCollectionNotNull(Collection<?> collection, String msg) {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void checkMapNotNull(Map<?, ?> collection, String msg) {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException(msg);
        }
    }
}
