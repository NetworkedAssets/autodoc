package com.networkedassets.autodoc.clients.integration.git.api;

import com.networkedassets.autodoc.clients.git.api.GitRepository;
import com.networkedassets.autodoc.clients.integration.IntegrationTest;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class GitRepositoryTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File folder;

    @Before
    public void createTempFolder() throws IOException {
        folder = temporaryFolder.newFolder();
        assertTrue(folder.isDirectory() && folder.listFiles().length == 0);
    }

    @Test
    public void verifyUrlIsProperlyBuiltWithPort() throws IOException, GitAPIException {
        final String projectKey = "AUT";
        final String repositorySlug = "autodoc";
        final String branchName = "refs/heads/master";
        final String username = "admin";
        final String password = "admin";
        URL urlWithPort = new URL("http://atlas.networkedassets.net:7990");

        GitRepository gitRepositoryWithPort = new GitRepository(urlWithPort, username, password);
        gitRepositoryWithPort.cloneRepository(folder.toPath(), projectKey, repositorySlug, branchName);

        assertTrue(folder.listFiles().length != 0);
    }

    @Test
    public void verifyUrlIsProperlyBuildWithoutPort() throws IOException, GitAPIException {
        final String projectKey = "DOC";
        final String repositorySlug = "doc";
        final String branchName = "refs/heads/master";
        final String username = "admin";
        final String password = "admin";
        URL urlWithoutPort = new URL("http://atlasdemo.networkedassets.net/bitbucket");

        GitRepository gitRepositoryWithoutPort = new GitRepository(urlWithoutPort, username, password);
        gitRepositoryWithoutPort.cloneRepository(folder.toPath(), projectKey, repositorySlug, branchName);

        assertTrue(folder.listFiles().length != 0);
    }
}
