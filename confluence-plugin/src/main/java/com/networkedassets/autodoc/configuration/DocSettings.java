package com.networkedassets.autodoc.configuration;

import net.java.ao.Entity;
import net.java.ao.schema.StringLength;

public interface DocSettings extends Entity {
    @StringLength(StringLength.UNLIMITED)
    String getTransformerUrl();
    @StringLength(StringLength.UNLIMITED)
    void setTransformerUrl(String url);
}
