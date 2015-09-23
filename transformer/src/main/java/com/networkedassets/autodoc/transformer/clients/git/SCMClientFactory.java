package com.networkedassets.autodoc.transformer.clients.git;

import com.networkedassets.autodoc.transformer.clients.git.api.SCM;

public interface SCMClientFactory {

  public SCM getSCMClient(SCMClientConfig config);
}
