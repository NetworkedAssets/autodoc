package com.networkedassets.autodoc.transformer;

import com.networkedassets.autodoc.transformer.settings.Settings;

/**
 * Created by mrobakowski on 10/2/2015.
 */
public class Response {

    public Settings body;
    public String raw;

    public Response(Settings body, String raw) {

        this.body = body;
        this.raw = raw;
    }
}
