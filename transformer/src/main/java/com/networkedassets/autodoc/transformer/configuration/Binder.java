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
        JavaDocGenerator javaDocGenerator = new JavaDocGenerator();
        SettingsManager settingsManager = new SettingsManager();
        EventHandler eventHandler = new EventHandler(settingsManager, javaDocGenerator);


        bind(settingsManager).to(SettingsManager.class);
        bind(javaDocGenerator).to(JavaDocGenerator.class);
        bind(eventHandler).to(EventHandler.class);
        bind(new Scheduler()).to(Scheduler.class);
        bind(new TestManager()).to(TestManager.class);
    }
}
