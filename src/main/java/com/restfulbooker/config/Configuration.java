package com.restfulbooker.config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "system:properties",
        "classpath:config/api.properties"
})
public interface Configuration extends Config {

    @Key("api.base.uri")
    String apiBaseUri();

    @Key("api.base.path")
    String apiBasePath();

    @Key("api.port")
    int apiPort();

    @Key("api.health.context")
    String apiHealthContext();

    @Key("auth.username")
    String authUsername();

    @Key("auth.password")
    String authPassword();
}
