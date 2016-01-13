package com.networkedassets.autodoc.transformer.handleRepoPush.provide.in;

import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;

/**
 * Created by mrobakowski on 11/12/2015.
 */
@FunctionalInterface
public interface PushEventProcessor {
    void processEvent(PushEvent event);
}
