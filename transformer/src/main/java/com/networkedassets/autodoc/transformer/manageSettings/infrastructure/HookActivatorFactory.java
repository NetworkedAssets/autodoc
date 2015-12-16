package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.manageSettings.require.HookActivator;
import com.networkedassets.autodoc.transformer.settings.Source;

import java.net.MalformedURLException;

/**
 * Created by kamil on 03.12.2015.
 */
public class HookActivatorFactory {

    public static HookActivator getInstance(Source source, String localhostAddress) throws MalformedURLException {
        HookActivator hookActivator = null;

        switch (source.getSourceType()) {
            //fallthrough!
            case STASH:
            case BITBUCKET:
                hookActivator = new AtlassianHookActivator(source, localhostAddress);
                break;
        }

        return hookActivator;
    }

}
