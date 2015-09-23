package com.networkedassets.autodoc.transformer.clients.git;

import com.networkedassets.autodoc.transformer.clients.git.api.GitStashSCM;
import com.networkedassets.autodoc.transformer.clients.git.api.SCM;

public class GitStashClientFactory implements SCMClientFactory {

  public SCM getSCMClient(SCMClientConfig config) {
    return new GitStashSCM(config);
  }
}
