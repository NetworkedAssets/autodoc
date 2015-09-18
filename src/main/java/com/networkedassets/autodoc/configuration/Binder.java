package com.networkedassets.autodoc.configuration;

import com.networkedassets.autodoc.EventHandler;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Created by kamil on 18.09.2015.
 */
public class Binder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(EventHandler.class).to(EventHandler.class);
    }
}
