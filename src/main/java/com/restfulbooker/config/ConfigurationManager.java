package com.restfulbooker.config;

import org.aeonbits.owner.ConfigFactory;

public class ConfigurationManager {

    private ConfigurationManager() {
    }

    private static final Configuration configuration = ConfigFactory.create(Configuration.class);

    public static Configuration getConfiguration() {
        return configuration;
    }
}
