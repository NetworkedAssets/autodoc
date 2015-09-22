package com.networkedassets.autodoc.transformer;

import com.networkedassets.autodoc.transformer.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.inject.Singleton;

/**
 * Handles incoming events
 */
public class EventHandler {

    public static Logger log = LoggerFactory.getLogger(EventHandler.class);

    public void handleEvent(Event event){
        //TODO: check if event is watched
        //TODO: if yes - send to javadoc generator
    }
}
