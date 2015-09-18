package com.networkedassets.autodoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

/**
 * Handles incoming events
 */
@Resource
@ManagedBean
public class EventHandler {

    public static Logger log = LoggerFactory.getLogger(EventHandler.class);

    public String test(){
        log.debug("IM AN EVENT HANDLER!!!!!!!!!!!!!!!!");
        return "EVENT HANDLER :D";
    }
}
