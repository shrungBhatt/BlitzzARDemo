package com.projects.shrungbhatt.blitzzardemo.utils;

import java.util.Vector;

public class ClassScope {
    private static java.lang.reflect.Field LIBRARIES;
    static {
        try {
            LIBRARIES = ClassLoader.class.getDeclaredField("loadedLibraryNames");
            LIBRARIES.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    public static String[] getLoadedLibraries(final ClassLoader loader) {
        final Vector<String> libraries;
        try {
            libraries = (Vector<String>) LIBRARIES.get(loader);
            return libraries.toArray(new String[] {});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}