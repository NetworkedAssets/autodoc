package com.networkedassets.autodoc.transformer.configuration;

import com.networkedassets.autodoc.transformer.*;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * Binder managing dependency injections in Jersey
 */
public class Binder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(new EventHandler()).to(EventHandler.class);
        bind(new JavaDocGenerator()).to(JavaDocGenerator.class);
        bind(new Scheduler()).to(Scheduler.class);
        bind(new SettingsManager()).to(SettingsManager.class);
        bind(new TestManager()).to(TestManager.class);
    }
}
