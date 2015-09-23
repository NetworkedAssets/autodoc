package com.networkedassets.autodoc.transformer.clients.git;

import java.net.URL;

import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClientConfig;


public class SCMClientConfig extends HttpClientConfig{

  public SCMClientConfig(URL baseUrl, String username, String password) {
    super(baseUrl, username, password);
  }

}
