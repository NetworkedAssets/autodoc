package com.networkedassets.autodoc.configureGui;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceSet;
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
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigureServlet extends HttpServlet {

    public static final String TEMPLATES_RESOURCE = "com.networkedassets.autodoc.configureGui:soy-templates";
    public static final String TEMPLATE_NAME = "com.networkedassets.autodoc.configureGui.configureScreen";
    private static final Gson GSON = new Gson();
    private static final Type LIST_BRANCHES_JSON_TYPE = new TypeToken<List<Project>>(){}.getType();
    private final PageBuilderService pageBuilderService;
    private final WebResourceManager webResourceManager;

    private SoyTemplateRenderer soyTemplateRenderer;
    private PageManager pageManager;
    private SpaceManager spaceManager;

    private TransformerServer transformerServer;

    public ConfigureServlet(SoyTemplateRenderer soyTemplateRenderer, PageManager pageManager,
                            SpaceManager spaceManager, PageBuilderService pageBuilderService, WebResourceManager webResourceManager) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
        this.pageBuilderService = pageBuilderService;
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

    private void renderConfigureScreen(HttpServletRequest req, HttpServletResponse resp, TransformerSettings settings)
            throws IOException, ServletException {

        List<Project> allProjects = settings.getProjectsStateForSpace(getSpaceKey(req));
        List<SimplePage> pages = getPages(req);
        String spaceKey = getSpaceKey(req);

        WebResourceAssembler assembler = pageBuilderService.assembler();
        assembler.resources().requireContext("autodoc_confluence");
        WebResourceSet resourcesSet = assembler.assembled().drainIncludedResources();
        StringWriter resources = new StringWriter();
        resourcesSet.writeHtmlTags(resources, UrlMode.AUTO);

        List<Map<String, ?>> allProjectsSoy = allProjects.stream().map(Project::toSoyData).collect(Collectors.toList());
        renderConfigureTemplateWithParams(resp, ImmutableMap.<String, Object>builder()
                        .put("resources", resources.toString())
                        .put("allProjects", allProjectsSoy)
                        .put("allProjectsJSON", GSON.toJson(allProjectsSoy))
                        .put("pages", pages)
                        .put("pagesJSON", GSON.toJson(pages))
                        .put("spaceKey", spaceKey)
                        .build()
        );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TransformerSettings settings = transformerServer.getSettings();
        renderConfigureScreen(req, resp, settings);
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
        String projectsJSON = req.getParameter("projectsJson");
        List<Project> projects = GSON.fromJson(projectsJSON, LIST_BRANCHES_JSON_TYPE);
        TransformerSettings settings = transformerServer.getSettings();
        settings.setProjectsStateForSpace(projects, getSpaceKey(req));

        transformerServer.saveSettings(settings);

        renderConfigureScreen(req, resp, settings);
    }
}