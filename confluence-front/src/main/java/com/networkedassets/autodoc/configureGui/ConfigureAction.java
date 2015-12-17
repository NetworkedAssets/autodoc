package com.networkedassets.autodoc.configureGui;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class ConfigureAction extends ConfluenceActionSupport {

	public String load() {
        return ConfluenceActionSupport.SUCCESS;
    }

    public String save() {
        return ConfluenceActionSupport.SUCCESS;
    }

	public String getInnerHtml() throws IOException {
		String html;
		html = IOUtils
				.toString(this.getClass().getClassLoader().getResourceAsStream("/configurationResources/index.html"));
		html = (html.split("<!--CUT-START-->")[1]).split("<!--CUT-END-->")[0];
		return html;
	}
}