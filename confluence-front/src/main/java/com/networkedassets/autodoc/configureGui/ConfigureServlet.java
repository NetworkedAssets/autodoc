package com.networkedassets.autodoc.configureGui;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.actions.SpaceAdminAction;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.spring.container.ContainerManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.ImmutableMap;
import com.networkedassets.autodoc.transformer.Response;
import com.networkedassets.autodoc.transformer.TransformerServer;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigureServlet extends HttpServlet {

    public static final String TEMPLATES_RESOURCE = "com.networkedassets.autodoc.confluence-front:soy-templates";
    public static final String TEMPLATE_NAME = "com.networkedassets.autodoc.configureGui.configureScreen";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final CollectionType LIST_PROJECTS_JSON_TYPE = OBJECT_MAPPER.getTypeFactory()
            .constructCollectionType(List.class, Project.class);
    public static final Logger log = LoggerFactory.getLogger(ConfigureServlet.class);

    private SoyTemplateRenderer soyTemplateRenderer;
    private PageManager pageManager;
    private SpaceManager spaceManager;

    private TransformerServer transformerServer;
    private SettingsManager settingsManager;

    public ConfigureServlet(SoyTemplateRenderer soyTemplateRenderer, PageManager pageManager,
                            SpaceManager spaceManager, SettingsManager settingsManager) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
        this.settingsManager = settingsManager;
        transformerServer = new TransformerServer(getTransformerUrl(), settingsManager.getGlobalSettings().getBaseUrl());
    }

    private Optional<Long> findDefaultUmlLocation(HttpServletRequest req) {
        return Optional.ofNullable(pageManager.getPage(getSpaceKey(req), "5. Building Block View")).map(Page::getId);
    }

    private Optional<Long> findDefaultJavadocLocation(HttpServletRequest req) {
        return Optional.ofNullable(pageManager.getPage(getSpaceKey(req), "5. Building Block View")).map(Page::getId);
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
            VelocityUtils.renderTemplateWithoutSwallowingErrors("configurationResources/configuration.vm", context,
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
        html = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("/configurationResources/index.html"));
        html = (html.split("<!--CUT-START-->")[1]).split("<!--CUT-END-->")[0];
        return html;
    }


    private String getSpaceKey(HttpServletRequest req) {
        return req.getParameter("key");
    }

    private List<SimplePage> getPages(HttpServletRequest req) {
        return pageManager.getPages(getSpace(req), true).stream().map(SimplePage::new).collect(Collectors.toList());
    }

    private Space getSpace(HttpServletRequest req) {
        return spaceManager.getSpace(getSpaceKey(req));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private String getTransformerUrl() {
        InputStream properties = ClassLoaderUtils.getResourceAsStream("autodoc_confluence.properties",
                getClass());
        Properties props = new Properties();
        try {
            props.load(properties);
        } catch (IOException e) {
            log.error("Couldn't load the configuration file", e);
        }
        return props.getProperty("transformerUrl", "http://localhost:8050");
    }
}