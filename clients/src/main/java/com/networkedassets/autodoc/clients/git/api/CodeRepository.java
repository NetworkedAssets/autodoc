package com.networkedassets.autodoc.clients.git.api;

import java.nio.file.Path;

public interface CodeRepository {

  public void cloneRepository(Path localRepoDirectory, String projectKey, String repositorySlug,
                              String branchName) throws Exception;
}
