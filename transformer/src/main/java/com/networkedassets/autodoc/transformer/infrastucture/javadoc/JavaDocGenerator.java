package com.networkedassets.autodoc.transformer.infrastucture.javadoc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkedassets.autodoc.clients.atlassian.api.ConfluenceClient;
import com.networkedassets.autodoc.clients.git.SCMClientConfig;
import com.networkedassets.autodoc.clients.git.api.GitStashSCM;
import com.networkedassets.autodoc.clients.git.api.SCM;
import com.networkedassets.autodoc.transformer.infrastucture.utlis.Consts;
import com.networkedassets.autodoc.transformer.infrastucture.utlis.CounfluenceFileConverter;
import com.networkedassets.autodoc.transformer.infrastucture.utlis.HtmlFileReader;
import com.networkedassets.autodoc.transformer.infrastucture.utlis.data.HtmlFile;
import com.networkedassets.autodoc.transformer.settings.SettingsForSpace;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocGenerator;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.DocResponse;

/**
 * Generates javadoc from provided code
 */
public class JavaDocGenerator implements DocGenerator{

	private static final String fileExtension = ".html";
	private Logger log = LoggerFactory.getLogger(JavaDocGenerator.class);
	protected Map<String, ConfluenceClient> clientMap = new HashMap<>();

	public void generate(@Nonnull String projectKey, @Nonnull String repoSlug, @Nonnull String branchId,
			@Nonnull Collection<SettingsForSpace> settingsForInterestedSpaces) throws JavadocException, IOException {
		if (settingsForInterestedSpaces.isEmpty())
			return;

		clientMap.clear();

		Path tmpDir = Files.createTempDirectory(null);
		SCM scmServer = getSCM();
		Path javadocDir = Javadoc.fromRepo(scmServer, tmpDir, projectKey, repoSlug, branchId);

		
		try (Stream<HtmlFile> htmlFiles = HtmlFileReader.read(javadocDir,
				new CounfluenceFileConverter(
						String.format(Consts.SUFFIX_TEMPLATE, projectKey, repoSlug, branchId.replace("/", "\\"))),
				fileExtension)) {
		}
	}

	
	protected SCM getSCM() throws MalformedURLException {
		return new GitStashSCM(new SCMClientConfig(new URL("http://46.101.240.138:7990"), "mrobakowski", "admin"));
	}



	@Override
	public DocResponse generate() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
