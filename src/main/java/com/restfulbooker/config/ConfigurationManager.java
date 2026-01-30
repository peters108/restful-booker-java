package com.restfulbooker.config;

import org.aeonbits.owner.ConfigFactory;

public class ConfigurationManager {

    private static final String CONFIG_RESOURCE = "config/api.properties";

    private ConfigurationManager() {
    }

    private static final Configuration CONFIG =
            ClasspathProperties.load(CONFIG_RESOURCE, Configuration.class)
                    .map(p -> ConfigFactory.create(Configuration.class, p))
                    .orElseGet(() -> ConfigFactory.create(Configuration.class));

    public static Configuration getConfiguration() {
        return CONFIG;
    }
}
