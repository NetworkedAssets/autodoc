package com.networkedassets.autodoc.presentationMacro;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import java.util.Map;

public class AutodocDashboard extends BaseMacro {
    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    @Override
    public String execute(Map map, String s, RenderContext renderContext) throws MacroException {
        return "<b>MACRO TEST</b>";
    }
}
