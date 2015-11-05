package com.networkedassets.autodoc.clients.git;

import com.networkedassets.autodoc.clients.git.api.SCM;

public interface SCMClientFactory {

  public SCM getSCMClient(SCMClientConfig config);
}
