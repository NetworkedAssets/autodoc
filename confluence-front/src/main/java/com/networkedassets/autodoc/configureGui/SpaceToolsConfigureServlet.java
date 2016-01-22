package com.networkedassets.autodoc.configureGui;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.actions.SpaceAdminAction;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.spring.container.ContainerManager;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SpaceToolsConfigureServlet extends HttpServlet {

    public static final String TEMPLATE_NAME = "com.networkedassets.autodoc.configureGui.configureScreen";

    public static final Logger log = LoggerFactory.getLogger(SpaceToolsConfigureServlet.class);

    private SoyTemplateRenderer soyTemplateRenderer;
    private PageManager pageManager;
    private SpaceManager spaceManager;

    private SettingsManager settingsManager;

    public SpaceToolsConfigureServlet(SoyTemplateRenderer soyTemplateRenderer, PageManager pageManager,
                                      SpaceManager spaceManager, SettingsManager settingsManager) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
        this.settingsManager = settingsManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        renderTemplate(req,resp);
    }

    private void renderTemplate(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            Context context = MacroUtils.createDefaultVelocityContext();
            context.put("pageContent", TEMPLATE_NAME);
            Space space = getSpace(req);
            context.put("space", space);
            context.put("innerHtml", getInnerHtml());
            SpaceAdminAction action = new SpaceAdminAction();
            ContainerManager.autowireComponent(action);
            action.setSpace(space);
            context.put("action", action);
            VelocityUtils.renderTemplateWithoutSwallowingErrors("configurationResources/spaceToolsConfiguration.vm", context,
                    resp.getWriter());
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new ServletException(e);
        }
    }

    private String getInnerHtml() throws IOException {
        String html;
        html = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("/configurationResources/configuration.html"));
        html = (html.split("<!--CUT-START-->")[1]).split("<!--CUT-END-->")[0];
        return html;
    }

    private String getSpaceKey(HttpServletRequest req) {
        return req.getParameter("key");
    }

    private Space getSpace(HttpServletRequest req) {
        return spaceManager.getSpace(getSpaceKey(req));
    }
}