package com.networkedassets.autodoc.transformer.util.uml;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.networkedassets.autodoc.clients.git.api.SCM;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantumldependency.cli.main.program.PlantUMLDependencyProgram;
import net.sourceforge.plantumldependency.commoncli.command.CommandLine;
import net.sourceforge.plantumldependency.commoncli.command.impl.CommandLineImpl;
import net.sourceforge.plantumldependency.commoncli.exception.CommandLineException;
import net.sourceforge.plantumldependency.commoncli.program.JavaProgram;
import net.sourceforge.plantumldependency.commoncli.program.execution.JavaProgramExecution;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

public class PlantUML {

	private static final String encoding = "UTF-8";
	private static final String objectFilter = "abstract_classes,classes,extensions,implementations,imports,interfaces,native_methods,static_imports";

	/**
	 * Convenience method for generating plantUML for a project on a SCM repo
	 *
	 * @param scmClient
	 *            git client using to clone repo
	 * @param localDirectory
	 *            where the repo should be cloned
	 * @param projectKey
	 *            stash's project key
	 * @param repositorySlug
	 *            stash's repo slug
	 * @param branchName
	 *            the name of the branch that is to be cloned
	 * @param diagramFilter
	 *            the names of object which will be showing on diagram ( def.
	 *            abstract_classes,classes,extensions,implementations,
	 *            imports,interfaces,native_methods,static_imports_
	 * @return plant UML description
	 * @throws PlantUMLException
	 */

	@Nonnull
	public static String fromRepo(@Nonnull SCM scmClient, @Nonnull Path localDirectory, @Nonnull String projectKey,
			@Nonnull String repositorySlug, @Nonnull String branchName, @Nullable String diagramFilter,
			@Nullable FileFormat fileformat) throws PlantUMLException {

		Preconditions.checkNotNull(scmClient);
		Preconditions.checkNotNull(localDirectory);
		Preconditions.checkNotNull(repositorySlug);
		Preconditions.checkNotNull(branchName);

		PlantUML plantUML = new PlantUML();
		createDirectoryIfNecessary(localDirectory);
		cloneTheRepo(scmClient, projectKey, repositorySlug, branchName, localDirectory);
		return plantUML.generateUmlDescription(localDirectory, diagramFilter);

	}

	/**
	 * Convenience method for generating plantUML for a project clone to
	 * localFileDirectory
	 *
	 * @param localDirectory
	 *            path to directory where project has been cloned
	 * @param diagramFilter
	 *            the names of object which will be showing on diagram ( def.
	 *            abstract_classes,classes,extensions,implementations,
	 *            imports,interfaces,native_methods,static_imports_
	 * @param fileFormat
	 *            the name of file extension
	 * @return plant UML description
	 * @throws PlantUMLException
	 */

	@Nonnull
	public static String fromDirectory(@Nonnull Path localDirectory, @Nullable String diagramFilter,
			@Nullable FileFormat fileFormat) throws PlantUMLException {

		Preconditions.checkNotNull(localDirectory);
		PlantUML plantUML = new PlantUML();
		return plantUML.generateUmlDescription(localDirectory, diagramFilter);

	}

	private String generateUmlDescription(@Nonnull Path localDirectory, @Nullable String diagramFilter)
			throws PlantUMLException {

		Preconditions.checkNotNull(localDirectory);
		String results = "";
		JavaProgramExecution plantumlDependencyProgramExecution;
		JavaProgram plantumlDependencyProgram;

		try {

			File tempFile = File.createTempFile("prefix-", "-suffix");
			tempFile.deleteOnExit();

			final CommandLine commandLineArguments = new CommandLineImpl(
					new String[] { "-o", tempFile.getAbsolutePath(), "-b", localDirectory.toFile().getAbsolutePath(),
							"-dt", Strings.isNullOrEmpty(diagramFilter) ? objectFilter : diagramFilter });
			plantumlDependencyProgram = new PlantUMLDependencyProgram();
			plantumlDependencyProgramExecution = plantumlDependencyProgram.parseCommandLine(commandLineArguments);
			plantumlDependencyProgramExecution.execute();
			results = new String(Files.readAllBytes(Paths.get(tempFile.getAbsolutePath())), encoding);

		} catch (MalformedURLException e) {
			throw new PlantUMLException("URL string is not parseable or contains an unsupported protocol", e);
		} catch (CommandLineException e) {
			throw new PlantUMLException("wrong commandline arguments", e);
		} catch (ParseException e) {
			throw new PlantUMLException("can't parse commandline arguments", e);
		}

		catch (IOException e) {
			throw new PlantUMLException("general I/O exception", e);
		}
		return results;
	}

	private static void cloneTheRepo(@Nonnull SCM scmServer, @Nonnull String projectKey, @Nonnull String repositorySlug,
			@Nonnull String branchName, @Nonnull Path localDirectory) throws PlantUMLException {
		try {

			Preconditions.checkNotNull(scmServer);
			Preconditions.checkNotNull(projectKey);
			Preconditions.checkNotNull(repositorySlug);
			Preconditions.checkNotNull(branchName);
			Preconditions.checkNotNull(localDirectory);

			scmServer.cloneRepository(localDirectory, projectKey, repositorySlug, branchName);
		} catch (Exception e) {
			throw new PlantUMLException("could not clone the repo", e);
		}
	}

	private static void createDirectoryIfNecessary(@Nonnull Path localDirectory) throws PlantUMLException {
		try {
			if (!Files.isDirectory(localDirectory)) {
				Files.createDirectories(localDirectory);
			}
		} catch (IOException e) {
			throw new PlantUMLException("could not create the directory: " + localDirectory, e);
		}
	}
}
