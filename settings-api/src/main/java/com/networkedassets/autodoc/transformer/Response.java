package com.networkedassets.autodoc.transformer;

import com.networkedassets.autodoc.transformer.settings.ConfluenceSettings;

/**
 * Created by mrobakowski on 10/2/2015.
 */
public class Response {

    public ConfluenceSettings body;
    public String raw;

    public Response(ConfluenceSettings body, String raw) {

        this.body = body;
        this.raw = raw;
    }
}
