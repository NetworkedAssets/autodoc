package com.networkedassets.autodoc.clients.git;

import java.net.URL;

import com.networkedassets.autodoc.clients.atlassian.HttpClientConfig;


public class SCMClientConfig extends HttpClientConfig{

  public SCMClientConfig(URL baseUrl, String username, String password) {
    super(baseUrl, username, password);
  }

}
