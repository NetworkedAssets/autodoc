package com.networkedassets.autodoc.transformer.clients.git.api;

import java.nio.file.Path;

public interface SCM {

  public void cloneRepository(Path localRepoDirectory, String projectKey, String repositorySlug,
                              String branchName) throws Exception;
}
