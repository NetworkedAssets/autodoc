package com.networkedassets.autodoc.configureGui;

import com.atlassian.confluence.spaces.actions.SpaceAdminAction;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class BranchSelectionAdminAction extends SpaceAdminAction {

    public String getInnerHtml() throws IOException {
        String html;
        html = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("/configurationResources/configuration.html"));
        html = (html.split("<!--CUT-START-->")[1]).split("<!--CUT-END-->")[0];
        return html;
    }
}
