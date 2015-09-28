package com.networkedassets.autodoc.transformer.clients.atlassian.api;

import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClient;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClientConfig;

public class ConfluenceClient extends HttpClient {

	public ConfluenceClient(HttpClientConfig config) {
		super(config);
	}

//	public void createOrUpdatePage(ConfluencePage page, String location) {
//        Optional<ConfluencePage> found = findPage(page);
//        if (found.isPresent()) {
//            page.setVersionInt(found.get().getVersionInt() + 1);
//            page.setId(found.get().getId());
//            updatePage(page);
//        } else {
//            createPage(page);
//        }
//    }

    public void removeJavadocPages() {

    }

}
