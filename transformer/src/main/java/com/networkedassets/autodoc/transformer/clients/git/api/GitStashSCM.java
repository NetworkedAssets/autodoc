package com.networkedassets.autodoc.transformer.clients.git.api;

import java.io.File;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.google.common.base.Preconditions;
import com.networkedassets.autodoc.transformer.clients.git.SCMClientConfig;

public class GitStashSCM implements SCM {

	@Nonnull
	private final URL baseUrl;
	@Nullable
	private final String username;
	@Nullable
	private final String password;

	public GitStashSCM(SCMClientConfig config) {

		this.baseUrl = config.getBaseUrl();
		this.username = config.getUsername();
		this.password = config.getPassword();

	}

	public void cloneRepository(@Nonnull File localRepoDirectory, @Nonnull String projectKey,
			@Nonnull String repositorySlug, @Nonnull String branchName)
					throws InvalidRemoteException, TransportException, GitAPIException {

		Preconditions.checkNotNull(localRepoDirectory);
		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repositorySlug);
		Preconditions.checkNotNull(branchName);

		StringBuffer uriTemplate = new StringBuffer();
		uriTemplate.append(this.baseUrl.getProtocol()).append("://%s@").append(this.baseUrl.getHost())
				.append(this.baseUrl.getPath()).append("/scm/%s/%s.git");

		String URI = String.format(uriTemplate.toString(), this.username, projectKey, repositorySlug);

		System.out.println(URI);
		Git.cloneRepository().setURI(URI).setDirectory(localRepoDirectory).setBranch(branchName)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.username, this.password)).call();

	}

}
