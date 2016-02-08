package com.networkedassets.autodoc.transformer.handleRepoPush.require;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.settings.Settings;

public interface DocumentationSender {
    /**
     * Sends documentation to Confluence
     * @param documentation documentation piece to be sent
     * @param settings current transformer settings
     * @return weather operation succeeded
     */
    boolean send(Documentation documentation, Settings settings);
}
