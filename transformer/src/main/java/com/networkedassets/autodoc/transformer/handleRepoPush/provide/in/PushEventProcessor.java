package com.networkedassets.autodoc.transformer.handleRepoPush.provide.in;

import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;


@FunctionalInterface
public interface PushEventProcessor {
    void processEvent(PushEvent event);
}
