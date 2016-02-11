package com.networkedassets.autodoc;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DocMacro implements Macro {

    @Override
    public String execute(Map<String, String> params, String s, ConversionContext conversionContext) throws MacroExecutionException {
        String dashboardSection;
        String resourcesPath = "download/resources/com.networkedassets.autodoc.confluence-front:dashboard-resources/dashboardResources/";

        try {
            dashboardSection = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("/dashboardResources/index.html"));
        } catch (IOException e) {
            throw new MacroExecutionException(e);
        }

        dashboardSection = ("<section" + dashboardSection.split("<section")[1]).split("section>")[0] + "section>";
  
        Map<String,Object> context = MacroUtils.defaultVelocityContext();
        
        context.put("dashboardSectionHtml", dashboardSection);
        context.put("resourcesPath", resourcesPath);

        try {
            context.put("paramsJson", new ObjectMapper().writeValueAsString(params));
        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }

        return VelocityUtils.getRenderedTemplate("/dashboardResources/dashboard.vm", context);
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }
}
