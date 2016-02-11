package com.networkedassets.autodoc.clients.git.api;

import java.net.URL;
import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.google.common.base.Preconditions;

public class GitRepository implements CodeRepository {

	@Nonnull
	private final URL baseUrl;
	@Nullable
	private final String username;
	@Nullable
	private final String password;

	public GitRepository(@Nonnull URL baseUrl, @Nullable String username, @Nullable String password) {

		Preconditions.checkNotNull(baseUrl);
		Preconditions.checkNotNull(username);
		Preconditions.checkNotNull(password);

		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;

	}

	public void cloneRepository(@Nonnull Path localRepoDirectory, @Nonnull String projectKey,
			@Nonnull String repositorySlug, @Nonnull String branchName)
					throws InvalidRemoteException, TransportException, GitAPIException {

		Preconditions.checkNotNull(localRepoDirectory);
		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repositorySlug);
		Preconditions.checkNotNull(branchName);

		String URI = String.format(getRepositoryPath(), this.username, projectKey, repositorySlug);

		Git.cloneRepository().setURI(URI).setDirectory(localRepoDirectory.toFile()).setBranch(branchName)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.username, this.password)).call();

	}

	private String getRepositoryPath() {

		StringBuffer uriTemplate = new StringBuffer();
		if (isPortSet()) {
			uriTemplate.append(this.baseUrl.getProtocol()).append("://%s@").append(this.baseUrl.getHost()).append(':')
					.append(this.baseUrl.getPort()).append(this.baseUrl.getPath()).append("/scm/%s/%s.git");
		} else {
			uriTemplate.append(this.baseUrl.getProtocol()).append("://%s@").append(this.baseUrl.getHost())
					.append(this.baseUrl.getPath()).append("/scm/%s/%s.git");
		}

		return uriTemplate.toString();

	}

	private boolean isPortSet() {
		return this.baseUrl.getPort() == -1 ? false : true;
	}

}
