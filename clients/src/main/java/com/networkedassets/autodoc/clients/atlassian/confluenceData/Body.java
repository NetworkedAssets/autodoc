package com.networkedassets.autodoc.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by mrobakowski on 9/28/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Body {
    private Storage storage;

    public Body() {
    }

    public Body(String contents) {
        storage = new Storage(contents);
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public String toString() {
        return "Body{" +
                "storage=" + storage +
                '}';
    }
}
