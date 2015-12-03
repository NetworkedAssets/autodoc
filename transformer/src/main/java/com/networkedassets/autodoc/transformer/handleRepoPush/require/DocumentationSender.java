package com.networkedassets.autodoc.transformer.handleRepoPush.require;

import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.settings.Settings;

public interface DocumentationSender {
    void send(Documentation documentation, Settings settings);
}
