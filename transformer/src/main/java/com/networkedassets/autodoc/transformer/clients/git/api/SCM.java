package com.networkedassets.autodoc.transformer.clients.git.api;

import java.io.File;

public interface SCM {

  public void cloneRepository(File localRepoDirectory, String projectKey, String repositorySlug,
      String branchName) throws Exception;
}
