package com.networkedassets.autodoc.transformer;

import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;

/**
 * Created by mrobakowski on 10/2/2015.
 */
public class Response {

    public SettingsForSpace body;
    public String raw;

    public Response(SettingsForSpace body, String raw) {

        this.body = body;
        this.raw = raw;
    }
}
