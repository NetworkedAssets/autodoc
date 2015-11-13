package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import com.networkedassets.autodoc.clients.git.SCMClientConfig;
import com.networkedassets.autodoc.clients.git.api.GitStashSCM;
import com.networkedassets.autodoc.clients.git.api.SCM;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.CodeProvider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class GitCodeProvider implements CodeProvider {
    private SCM git;

    public GitCodeProvider(SCM git) {
        if (git == null) {
            try {
                this.git = new GitStashSCM(new SCMClientConfig(new URL("http://46.101.240.138:7990"), "mrobakowski", "admin"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return;
        }
        this.git = git;
    }

    @Override
    public Code getCode(String projectKey, String repoSlug, String branchId) {
        Path temp;

        try {
            temp = Files.createTempDirectory(null);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create a temp dir!", e);
        }

        try {
            git.cloneRepository(temp, projectKey, repoSlug, branchId);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't clone the repository!", e);
        }

        return new Code(temp);
    }
}
