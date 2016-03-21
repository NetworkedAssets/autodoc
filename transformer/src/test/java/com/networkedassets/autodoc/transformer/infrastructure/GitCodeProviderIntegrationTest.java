package com.networkedassets.autodoc.transformer.infrastructure;

import java.io.IOException;

import com.networkedassets.autodoc.integration.IntegrationTest;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure.GitCodeProvider;
import com.networkedassets.autodoc.transformer.settings.Source;

@Category(IntegrationTest.class)
public class GitCodeProviderIntegrationTest {

	// To do: add some test to clone repository

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private Git remote;

	@Before
	public void setUp() throws GitAPIException, IOException {
		remote = Git.init().setDirectory(tempFolder.newFolder("remote")).call();
		remote.commit().setMessage("Initial commit").call();
		remote.branchCreate().setName("develop").call();

	}

	@After
	public void tearDown() {
		remote.close();
	}

	@Test(expected = RuntimeException.class)
	public void testGetCodeGivenNoProperUrlThrowsRuntimeException() throws Exception {

		Source source = new Source();
		source.setPassword("-");
		source.setUsername("-");
		source.setUrl("-");

		GitCodeProvider gitProvider = new GitCodeProvider();
		gitProvider.getCode(source, "", "", "");

	}

}
