package com.networkedassets.autodoc.transformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.clients.atlassian.api.ConfluenceClient;
import com.networkedassets.autodoc.transformer.clients.git.api.SCM;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import com.networkedassets.autodoc.transformer.uml.PlantUML;
import com.networkedassets.autodoc.transformer.uml.PlantUMLException;
import com.networkedassets.autodoc.transformer.utils.HtmlFileReader;
import com.networkedassets.autodoc.transformer.utils.PlantUMLFileConverter;
import com.networkedassets.autodoc.transformer.utils.data.HtmlFile;

public class PlantUmlGenerator extends JavaDocGenerator {

	private static final String fileExtension = ".html";
	private static final String umlPrefix = "UML ";
	private Logger log = LoggerFactory.getLogger(PlantUmlGenerator.class);

	public void generateFromStashAndPost(@Nonnull String projectKey, @Nonnull String repoSlug, @Nonnull String branchId,
			@Nonnull Collection<SettingsForSpace> settingsForInterestedSpaces) throws IOException {
		if (settingsForInterestedSpaces.isEmpty())
			return;
		String plantUmlDescription = "";
		Path tmpDir = Files.createTempDirectory(null);
		SCM scmServer = getSCM();
		try {
			plantUmlDescription = PlantUML.fromRepo(scmServer, tmpDir, projectKey, repoSlug, branchId, null, null);
		} catch (PlantUMLException e1) {
			log.error("Could not generate plant uml description", e1);
		}

		removeOldJavadoc(umlPrefix + projectKey, repoSlug, branchId,
				settingsForInterestedSpaces.stream().map(SettingsForSpace::getConfluenceSpace));

		try (Stream<HtmlFile> htmlFiles = HtmlFileReader.read(tmpDir,
				new PlantUMLFileConverter(plantUmlDescription,
						String.format(" [%s/%s/%s]", umlPrefix + projectKey, repoSlug, branchId), null),
				fileExtension)) {
			htmlFiles.forEach(htmlFile -> settingsForInterestedSpaces.forEach(cs -> {
				ConfluenceClient confluence = getConfluenceForUrl(cs.getConfluenceUrl());
				if (confluence != null) {
					try {
						confluence.createJavadocPage(cs.getSpaceKey(), umlPrefix + projectKey, repoSlug, branchId,
								umlPrefix + htmlFile.getAdditionalProperties().get("packageName").toString() + "."
										+ htmlFile.getAdditionalProperties().get("className").toString(),
								htmlFile.getFileContent(), cs.getProjectByKey(projectKey).getRepoBySlug(repoSlug)
										.getBranchById(branchId).umlPageId);
					} catch (UnirestException e2) {
						log.error("Could not create the page", e2);
					}
				}
			}));
		}
	}

}
