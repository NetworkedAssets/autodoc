package com.networkedassets.autodoc.configureGui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.context.Context;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.actions.SpaceAdminAction;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.spring.container.ContainerManager;

public class ConfigureServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static final String TEMPLATE_NAME = "com.networkedassets.autodoc.configureGui.configureScreen";
	private SpaceManager spaceManager;

	public ConfigureServlet(SpaceManager spaceManager) {

		this.spaceManager = spaceManager;

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		renderTemplate(req, resp);
	}

	private void renderTemplate(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
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
		html = IOUtils
				.toString(this.getClass().getClassLoader().getResourceAsStream("/configurationResources/index.html"));
		html = (html.split("<!--CUT-START-->")[1]).split("<!--CUT-END-->")[0];
		return html;
	}

	private String getSpaceKey(HttpServletRequest req) {
		return req.getParameter("key");
	}

	private Space getSpace(HttpServletRequest req) {
		return spaceManager.getSpace(getSpaceKey(req));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

}