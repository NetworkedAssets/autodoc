package com.networkedassets.autodoc.clients.git;

import com.networkedassets.autodoc.clients.git.api.SCM;
import com.networkedassets.autodoc.clients.git.api.GitStashSCM;

public class GitStashClientFactory implements SCMClientFactory {

  public SCM getSCMClient(SCMClientConfig config) {
    return new GitStashSCM(config);
  }
}
