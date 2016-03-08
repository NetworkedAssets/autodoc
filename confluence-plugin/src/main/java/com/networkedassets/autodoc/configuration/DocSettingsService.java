package com.networkedassets.autodoc.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.core.util.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("WeakerAccess")
public class DocSettingsService {
    private final Logger log = LoggerFactory.getLogger(DocSettingsService.class);
    private final ActiveObjects ao;

    public DocSettingsService(ActiveObjects ao) {
        this.ao = ao;
    }

    public String getTransformerUrl() {
        return ao.executeInTransaction(() -> {
            String url;
            DocSettings[] docSettingses = ao.find(DocSettings.class);
            if (docSettingses.length < 1) {
                url = getTransformerUrlFromConfigFile();
                DocSettings docSettings = ao.create(DocSettings.class);
                docSettings.setTransformerUrl(url);
                docSettings.save();
            } else {
                url = docSettingses[0].getTransformerUrl();
            }
            return url;
        });
    }

    private String getTransformerUrlFromConfigFile() {
        InputStream properties = ClassLoaderUtils.getResourceAsStream("autodoc_confluence.properties", getClass());
        Properties props = new Properties();
        try {
            props.load(properties);
        } catch (IOException e) {
            log.error("Couldn't load the configuration file", e);
        }
        return props.getProperty("transformerUrl", "https://localhost:8050/");
    }
}
