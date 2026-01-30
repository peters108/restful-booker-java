package com.restfulbooker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Loads a .properties resource from the classpath, trying the context class
 * loader first (so tests see test-classes) then the given class's loader (so
 * main sees classes). Use this when Owner or other code resolves classpath
 * resources with a loader that doesn't see both in your environment.
 */
public final class ClasspathProperties {

    private ClasspathProperties() {
    }

    /**
     * Loads the named resource (e.g. "config/api.properties") as Properties.
     *
     * @param resourceName classpath resource name, no leading slash
     * @param classLoaderFallback class whose loader to try if context loader doesn't find the resource (e.g. YourConfig.class)
     * @return the loaded properties, or empty if the resource was not found
     */
    public static Optional<Properties> load(String resourceName, Class<?> classLoaderFallback) {
        ClassLoader context = Thread.currentThread().getContextClassLoader();
        ClassLoader fallback = classLoaderFallback != null ? classLoaderFallback.getClassLoader() : null;
        for (ClassLoader loader : new ClassLoader[]{context, fallback}) {
            if (loader == null) continue;
            try (InputStream in = loader.getResourceAsStream(resourceName)) {
                if (in != null) {
                    Properties p = new Properties();
                    p.load(in);
                    return Optional.of(p);
                }
            } catch (IOException ignored) {
                // try next loader
            }
        }
        return Optional.empty();
    }
}
