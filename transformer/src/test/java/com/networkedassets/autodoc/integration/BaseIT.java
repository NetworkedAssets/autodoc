package com.networkedassets.autodoc.integration;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.*;

/**
 * Created by mtulaza on 2016-03-21.
 */
public abstract class BaseIT {
    public static Logger log = LoggerFactory.getLogger(BaseIT.class);

    /**
     * test run before every integration test to ensure
     * that host, port and path are available
     */
    @BeforeClass
    public static void testPropertiesToRunIntegrationTestsObtained() {
        int port = TransformerConstants.getPort();
        String host = TransformerConstants.getHost();
        String path = TransformerConstants.getPath();

        log.info("transformer data obtained: host: [{}]  port: [{}]  path: [{}]", host, port, path);

        Assert.assertThat(port, not(equalTo(0)));
        Assert.assertThat(host, not(isEmptyOrNullString()));
    }
}
