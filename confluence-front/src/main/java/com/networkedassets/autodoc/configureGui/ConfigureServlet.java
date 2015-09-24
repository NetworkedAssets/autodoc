package com.networkedassets.autodoc.configureGui;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.actions.SpaceAdminAction;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.networkedassets.autodoc.transformer.TransformerServer;
import com.networkedassets.autodoc.transformer.TransformerServerMock;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.TransformerSettings;
import org.apache.velocity.context.Context;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConfigureServlet extends HttpServlet {

    public static final String TEMPLATES_RESOURCE = "com.networkedassets.autodoc.confluence-front:soy-templates";
    public static final String TEMPLATE_NAME = "com.networkedassets.autodoc.configureGui.configureScreen";
    private static final Gson GSON = new Gson();
    private static final Type LIST_PROJECTS_JSON_TYPE = new TypeToken<List<Project>>(){}.getType();

    private SoyTemplateRenderer soyTemplateRenderer;
    private PageManager pageManager;
    private SpaceManager spaceManager;

    private TransformerServer transformerServer;

    public ConfigureServlet(SoyTemplateRenderer soyTemplateRenderer, PageManager pageManager,
                            SpaceManager spaceManager) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
        transformerServer = new TransformerServerMock("localhost:8099");
    }

    private void renderConfigureTemplateWithParams(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> templateParams)
            throws IOException, ServletException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            Context context = MacroUtils.createDefaultVelocityContext();
            context.put("soyRenderer", soyTemplateRenderer);
            context.put("soyParams", templateParams);
            context.put("soyResource", TEMPLATES_RESOURCE);
            context.put("pageContent", TEMPLATE_NAME);
            Space space = getSpace(req);
            context.put("space", space);
            SpaceAdminAction action = new SpaceAdminAction();
            ContainerManager.autowireComponent(action);
            action.setSpace(space);
            context.put("action", action);
            VelocityUtils.renderTemplateWithoutSwallowingErrors("templates/space-admin-decorator.vm", context,
                    resp.getWriter());
        } catch (Exception e) {
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
        Optional<Long> defaultJavadocLocation = findDefaultJavadocLocation(req);
        Optional<Long> defaultUmlLocation = findDefaultUmlLocation(req);
        defaultJavadocLocation.ifPresent(pageId -> allProjects.forEach(p -> p.setDefaultJavadocLocation(pageId)));
        defaultUmlLocation.ifPresent(pageId -> allProjects.forEach(p -> p.setDefaultUmlLocation(pageId)));
        List<SimplePage> pages = getPages(req);

        List<Map<String, ?>> allProjectsSoy = allProjects.stream().map(Project::toSoyData).collect(Collectors.toList());
        renderConfigureTemplateWithParams(req, resp, ImmutableMap.<String, Object>builder()
                        .put("allProjects", allProjectsSoy)
                        .put("pages", pages)
                        .put("message", message)
                        .build()
        );
    }

    private Optional<Long> findDefaultUmlLocation(HttpServletRequest req) {
        return Optional.ofNullable(pageManager.getPage(getSpaceKey(req), "5. Building Block View")).map(Page::getId);
    }

    private Optional<Long> findDefaultJavadocLocation(HttpServletRequest req) {
        return Optional.ofNullable(pageManager.getPage(getSpaceKey(req), "5. Building Block View")).map(Page::getId);
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
//        List<Project> projects = GSON.fromJson(newSettings, LIST_PROJECTS_JSON_TYPE);
        TransformerSettings settings = transformerServer.getSettings();
//        settings.setProjectsStateForSpace(projects, getSpaceKey(req));

        transformerServer.saveSettings(settings);

        renderConfigureScreen(req, resp, settings, newSettings);
    }
}