package com.networkedassets.autodoc.presentationMacro;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;

import java.io.IOException;
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
        String dashboardSection;
        String resourcesPath = "download/resources/com.networkedassets.autodoc.confluence-front:dashboard-resources/dashboardResources/";

        try {
            dashboardSection = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("/dashboardResources/index.html"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dashboardSection = ("<section" + dashboardSection.split("<section")[1]).split("section>")[0] + "section>";

        VelocityContext context = new VelocityContext(MacroUtils.defaultVelocityContext());
        context.put("dashboardSectionHtml", dashboardSection);
        context.put("resourcesPath", resourcesPath);


        return VelocityUtils.getRenderedTemplate("/dashboardResources/dashboard.vm", context);
    }
}
