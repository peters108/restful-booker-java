package com.restfulbooker.hooks;

import com.restfulbooker.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * Cucumber hooks for scenario setup and teardown.
 * Initializes and clears {@link ScenarioContext}, logs scenario lifecycle, and optionally attaches failure details to Allure.
 */
public class TestHooks {

    private static final Logger log = LoggerFactory.getLogger(TestHooks.class);

    private static final ThreadLocal<ScenarioContext> SCENARIO_CONTEXT = new ThreadLocal<>();

    @Before
    public void setUp(Scenario scenario) {
        SCENARIO_CONTEXT.set(new ScenarioContext());
        log.info(">>> Scenario started: {}", scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            log.error(">>> Scenario failed: {}", scenario.getName());
            try {
                String failureInfo = "Scenario failed: " + scenario.getName();
                Allure.getLifecycle().addAttachment("Failure", "text/plain", "txt",
                        failureInfo.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.warn("Could not add Allure attachment: {}", e.getMessage());
            }
        } else {
            log.info(">>> Scenario finished: {}", scenario.getName());
        }
        ScenarioContext context = SCENARIO_CONTEXT.get();
        if (context != null) {
            context.clear();
        }
        SCENARIO_CONTEXT.remove();
    }

    /**
     * Returns the current scenario's context for use in step definitions.
     */
    public static ScenarioContext getScenarioContext() {
        return SCENARIO_CONTEXT.get();
    }
}
