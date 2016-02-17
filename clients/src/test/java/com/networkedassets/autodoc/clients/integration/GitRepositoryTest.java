package com.networkedassets.autodoc.clients.integration;

import com.networkedassets.autodoc.clients.git.api.GitRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/*
 *   shouldn't these test be treated as integration tests?
*/
@Category(IntegrationTest.class)
public class GitRepositoryTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void verifyUrlIsProperlyBuiltWithPort() throws IOException, GitAPIException {
        File folder = temporaryFolder.newFolder();
        final String projectKey = "AUT";
        final String repositorySlug = "autodoc";
        final String branchName = "refs/heads/master";
        final String username = "admin";
        final String password = "admin";
        URL urlWithPort = new URL("http://atlas.networkedassets.net:7990");

        assertTrue(folder.isDirectory());
        assertTrue(folder.listFiles().length == 0);

        GitRepository gitRepositoryWithPort = new GitRepository(urlWithPort, username, password);
        gitRepositoryWithPort.cloneRepository(folder.toPath(), projectKey, repositorySlug, branchName);

        assertTrue(folder.listFiles().length != 0);
    }

    @Test
    public void verifyUrlIsProperlyBuildWithoutPort() throws IOException, GitAPIException {
        File folder = temporaryFolder.newFolder();
        final String projectKey = "DOC";
        final String repositorySlug = "doc";
        final String branchName = "refs/heads/master";
        final String username = "admin";
        final String password = "admin";
        URL urlWithoutPort = new URL("http://atlasdemo.networkedassets.net/bitbucket");

        assertTrue(folder.isDirectory());
        assertTrue(folder.listFiles().length == 0);

        GitRepository gitRepositoryWithoutPort = new GitRepository(urlWithoutPort, username, password);
        gitRepositoryWithoutPort.cloneRepository(folder.toPath(), projectKey, repositorySlug, branchName);

        assertTrue(folder.listFiles().length != 0);
    }
}
