package com.networkedassets.autodoc.transformer;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.clients.atlassian.api.ConfluenceClient;
import com.networkedassets.autodoc.clients.git.SCMClientConfig;
import com.networkedassets.autodoc.clients.git.api.GitStashSCM;
import com.networkedassets.autodoc.clients.git.api.SCM;
import com.networkedassets.autodoc.transformer.infrastucture.javadoc.Javadoc;
import com.networkedassets.autodoc.transformer.infrastucture.javadoc.JavadocException;
import com.networkedassets.autodoc.transformer.settings.ConfluenceSpace;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import com.networkedassets.autodoc.transformer.utils.Consts;
import com.networkedassets.autodoc.transformer.utils.CounfluenceFileConverter;
import com.networkedassets.autodoc.transformer.utils.HtmlFileReader;
import com.networkedassets.autodoc.transformer.utils.data.HtmlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates javadoc from provided code
 */
public class JavaDocGenerator {

	private static final String fileExtension = ".html";
	private Logger log = LoggerFactory.getLogger(JavaDocGenerator.class);
	protected Map<String, ConfluenceClient> clientMap = new HashMap<>();

	public void generateFromStashAndPost(@Nonnull String projectKey, @Nonnull String repoSlug, @Nonnull String branchId,
			@Nonnull Collection<SettingsForSpace> settingsForInterestedSpaces) throws JavadocException, IOException {
		if (settingsForInterestedSpaces.isEmpty())
			return;

		clientMap.clear();

		Path tmpDir = Files.createTempDirectory(null);
		SCM scmServer = getSCM();
		Path javadocDir = Javadoc.fromStashRepo(scmServer, tmpDir, projectKey, repoSlug, branchId);

		removeOldJavadoc(projectKey, repoSlug, branchId,
				settingsForInterestedSpaces.stream().map(SettingsForSpace::getConfluenceSpace));
		try (Stream<HtmlFile> htmlFiles = HtmlFileReader.read(javadocDir,
				new CounfluenceFileConverter(
						String.format(Consts.SUFFIX_TEMPLATE, projectKey, repoSlug, branchId.replace("/", "\\"))),
				fileExtension)) {
			htmlFiles.forEach(htmlFile -> settingsForInterestedSpaces.forEach(cs -> {
				ConfluenceClient confluence = getConfluenceForUrl(cs.getConfluenceUrl());
				if (confluence != null) {
					try {
						confluence.createJavadocPage(cs.getSpaceKey(), projectKey, repoSlug,
								branchId.replace("/", "\\"),
								htmlFile.getAdditionalProperties().get("packageName").toString() + "."
										+ htmlFile.getAdditionalProperties().get("className").toString(),
								htmlFile.getFileContent(), cs.getProjectByKey(projectKey).getRepoBySlug(repoSlug)
										.getBranchById(branchId).javadocPageId);
					} catch (UnirestException e) {
						log.error("Could not create the page", e);
					}
				}
			}));
		}
	}

	private Set<Map.Entry<String, List<ConfluenceSpace>>> groupByUrl(Stream<ConfluenceSpace> interestedSpaces) {
		return interestedSpaces.collect(Collectors.groupingBy(ConfluenceSpace::getConfluenceUrl)).entrySet();
	}

	protected void removeOldJavadoc(String projectKey, String repoSlug, String branchId,
			Stream<ConfluenceSpace> interestedSpaces) {
		groupByUrl(interestedSpaces).forEach(entry -> {
			final String url = entry.getKey();
			final ConfluenceClient client = getConfluenceForUrl(url);

			if (client != null) {
				entry.getValue().stream().map(ConfluenceSpace::getSpaceKey).forEach(spaceKey -> {
					try {
						client.removeJavadocPages(spaceKey, projectKey, repoSlug, branchId);
					} catch (UnirestException e) {
						log.error("Could not remove javadoc pages", e);
					}
				});
			}

		});
	}

	// TODO: make this configurable
	protected ConfluenceClient getConfluenceForUrl(String url) {
		try {
			if (clientMap.containsKey(url))
				return clientMap.get(url);

			ConfluenceClient client = new ConfluenceClient(new HttpClientConfig(new URL(url), "mrobakowski", "admin"));
			clientMap.put(url, client);
			return client;
		} catch (MalformedURLException e) {
			log.error("Malformed URL: " + url, e);
			return null;
		}
	}

	// TODO: make this configurable
	protected SCM getSCM() throws MalformedURLException {
		return new GitStashSCM(new SCMClientConfig(new URL("http://46.101.240.138:7990"), "mrobakowski", "admin"));
	}
}
