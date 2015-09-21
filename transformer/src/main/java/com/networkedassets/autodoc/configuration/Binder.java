package com.networkedassets.autodoc.configuration;

import com.networkedassets.autodoc.EventHandler;
import com.networkedassets.autodoc.JavaDocGenerator;
import com.networkedassets.autodoc.Scheduler;
import com.networkedassets.autodoc.SettingsManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Created by kamil on 18.09.2015.
 */
public class Binder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(EventHandler.class).to(EventHandler.class);
        bind(JavaDocGenerator.class).to(JavaDocGenerator.class);
        bind(Scheduler.class).to(Scheduler.class);
        bind(SettingsManager.class).to(Scheduler.class);
    }
}
