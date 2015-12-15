package com.networkedassets.autodoc.clients.atlassian.api;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.clients.atlassian.confluenceData.ConfluencePage;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mtulaza on 2015-12-14.
 */
public class ConfluenceClientTest {
    private final String CONFLUENCE_URL = "http://46.101.240.138:8090";
    private final String CONFLUENCE_USERNAME = "kcala";
    private final String CONFLUENCE_PASSWORD = "admin";

    private ConfluenceClient confluenceClient;

    public ConfluenceClientTest() throws MalformedURLException {
        HttpClientConfig httpClientConfig = new HttpClientConfig(new URL(CONFLUENCE_URL), CONFLUENCE_USERNAME, CONFLUENCE_PASSWORD);
        confluenceClient = new ConfluenceClient(httpClientConfig);
    }

    @Test
    public void testConfluenceClient() {
        assertNotNull(confluenceClient);
    }

    @Test
    public void testCreatePageAndThenRemove() throws UnirestException {
        ConfluencePage confluencePage = confluenceClient.createPage(RandomStringUtils.randomAlphabetic(10), "spac", "CONTENTS-TEST", "javadoc");
        assertNotNull(confluencePage);
        confluenceClient.removePage(confluencePage.getId());
    }

    @Test(expected = RuntimeException.class)
    public void testCannotCreatePageWithTheSameNameAndThenRemove() throws UnirestException {
        final String name = "PAGE_NAME_THAT_ALREADY_EXISTS";
        // adding confluence page
        ConfluencePage confluencePage = confluenceClient.createPage(name, "spac", "CONTENTS-TEST", "javadoc");

        try{
            // trying to add a page with the same name, should throw RuntimeException
            confluenceClient.createPage(name, "spac", "CONTENTS-TEST", "javadoc");
        }finally {
            confluenceClient.removePage(confluencePage.getId());
        }
    }

    /**
     * confluence needs some time to publish created page before finding it
     * 5 seconds should be enough to do so
     * that is why the test is marked as @Ignored, remove it if you want to run the test
     * ( tested about 10 times one-by-one and passed (with 5 seconds delay) )
     * @throws UnirestException
     * @throws InterruptedException
     */
    @Test
    @Ignore
    public void testCreatePageThenFindThatPageAndThenRemove() throws UnirestException, InterruptedException {
        final String name = "some-name";
        ConfluencePage createdPage = confluenceClient.createPage(name, "spac", "CONTENTS-TEST", "javadoc");
        Thread.sleep(5000);
        ConfluencePage foundPage = confluenceClient.findPage(createdPage.getSpaceKey(), createdPage.getTitle()).get();
        assertNotNull(foundPage);
        Assert.assertThat(foundPage, CoreMatchers.not(Optional.empty()));

        confluenceClient.removePage(createdPage.getId());
    }

    @Test
    public void testCreatePagePutLabelAndThenRemove() throws UnirestException {
        ConfluencePage confluencePage = confluenceClient.createPage(RandomStringUtils.randomAlphabetic(10), "spac", "SOME CONTENT", "javadoc");
        boolean putLabelResult = confluenceClient.putLabel(confluencePage.getId(), "LABEL1 LABEL2 LABEL3");
        assertTrue(putLabelResult);
        confluenceClient.removePage(confluencePage.getId());
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsNullPointerExceptionWhenNullPassed() throws UnirestException {
        //guava Precondition test
        confluenceClient.createJavadocPage(null, null, null, null, null, null, null);
    }
}
