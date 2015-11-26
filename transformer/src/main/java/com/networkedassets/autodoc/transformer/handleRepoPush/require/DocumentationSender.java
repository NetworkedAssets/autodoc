package com.networkedassets.autodoc.transformer.handleRepoPush.require;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.settings.ConfluenceSettings;

import java.util.Collection;

public interface DocumentationSender {
    void send(Documentation documentation, ConfluenceSettings confluenceSettings);
}
