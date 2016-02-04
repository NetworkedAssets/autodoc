package com.networkedassets.autodoc.configuration;

import com.atlassian.confluence.pages.Page;

public class SimplePage {

    private final String title;
    private final String id;

    public SimplePage(Page page) {
        title = page.getDisplayTitle();
        id = Long.toString(page.getId());
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

}
