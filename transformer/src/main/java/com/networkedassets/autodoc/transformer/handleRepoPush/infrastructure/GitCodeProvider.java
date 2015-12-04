package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import com.networkedassets.autodoc.clients.git.api.GitRepository;
import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.require.CodeProvider;
import com.networkedassets.autodoc.transformer.settings.Source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by mrobakowski on 11/13/2015.
 */
public class GitCodeProvider implements CodeProvider {

	@Override
	public Code getCode(Source source, String projectKey, String repoSlug, String branchId) {
		Path temp;

		try {
			temp = Files.createTempDirectory(null);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create a temp dir!", e);
		}

		try {
			getGitRepository(source).cloneRepository(temp, projectKey, repoSlug, branchId);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't clone the repository!", e);
		}

		return new Code(temp);
	}

	private GitRepository getGitRepository(Source source) throws MalformedURLException {
		return new GitRepository(new URL(source.getUrl()), source.getUsername(), source.getPassword());

	}

}
