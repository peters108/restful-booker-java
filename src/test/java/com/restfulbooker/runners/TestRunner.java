package com.restfulbooker.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.restfulbooker.stepdefs", "com.restfulbooker.hooks"},
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "json:target/cucumber-reports/Cucumber.json",
                "junit:target/cucumber-reports/Cucumber.xml"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
