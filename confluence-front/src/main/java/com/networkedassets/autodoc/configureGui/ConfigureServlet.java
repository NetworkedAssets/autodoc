package com.networkedassets.autodoc.configureGui;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.networkedassets.autodoc.transformer.TransformerServer;
import com.networkedassets.autodoc.transformer.TransformerServerMock;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.TransformerSettings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigureServlet extends HttpServlet {

    public static final String TEMPLATES_RESOURCE = "com.networkedassets.autodoc.confluence-front:soy-templates";
    public static final String TEMPLATE_NAME = "com.networkedassets.autodoc.configureGui.configureScreen";
    private static final Gson GSON = new Gson();
    private static final Type LIST_PROJECTS_JSON_TYPE = new TypeToken<List<Project>>(){}.getType();
    private final WebResourceManager webResourceManager;

    private SoyTemplateRenderer soyTemplateRenderer;
    private PageManager pageManager;
    private SpaceManager spaceManager;

    private TransformerServer transformerServer;

    public ConfigureServlet(SoyTemplateRenderer soyTemplateRenderer, PageManager pageManager,
                            SpaceManager spaceManager, WebResourceManager webResourceManager) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
        this.webResourceManager = webResourceManager;
        transformerServer = new TransformerServerMock("localhost:8099");
    }

    private void renderConfigureTemplateWithParams(HttpServletResponse resp, Map<String, Object> templateParams)
            throws IOException, ServletException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            soyTemplateRenderer.render(resp.getWriter(), TEMPLATES_RESOURCE,
                    TEMPLATE_NAME, templateParams);
        } catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new ServletException(e);
        }
    }

    private void renderConfigureScreen(HttpServletRequest req, HttpServletResponse resp, TransformerSettings settings,
                                       String message)
            throws IOException, ServletException {

        List<Project> allProjects = settings.getProjectsStateForSpace(getSpaceKey(req));
        List<SimplePage> pages = getPages(req);
        String spaceKey = getSpaceKey(req);

        String resources = webResourceManager.getResourceTags("com.networkedassets.autodoc.confluence-front:autodoc_confluence-resources", UrlMode.AUTO)
                + webResourceManager.getResourceTags("com.networkedassets.autodoc.confluence-front:soy-templates", UrlMode.AUTO);

        List<Map<String, ?>> allProjectsSoy = allProjects.stream().map(Project::toSoyData).collect(Collectors.toList());
        renderConfigureTemplateWithParams(resp, ImmutableMap.<String, Object>builder()
                        .put("resources", resources)
                        .put("allProjects", allProjectsSoy)
                        .put("allProjectsJSON", GSON.toJson(allProjectsSoy))
                        .put("pages", pages)
                        .put("pagesJSON", GSON.toJson(pages))
                        .put("spaceKey", spaceKey)
                        .put("message", message)
                        .build()
        );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TransformerSettings settings = transformerServer.getSettings();
        renderConfigureScreen(req, resp, settings, "");
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
        String newSettings = req.getParameter("newSettings");
        List<Project> projects = GSON.fromJson(newSettings, LIST_PROJECTS_JSON_TYPE);
        TransformerSettings settings = transformerServer.getSettings();
        settings.setProjectsStateForSpace(projects, getSpaceKey(req));

        transformerServer.saveSettings(settings);

        renderConfigureScreen(req, resp, settings, newSettings);
    }
}