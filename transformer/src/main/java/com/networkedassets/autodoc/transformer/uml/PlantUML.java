package com.networkedassets.autodoc.transformer.uml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.networkedassets.autodoc.transformer.clients.git.api.SCM;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantumldependency.cli.main.program.PlantUMLDependencyProgram;
import net.sourceforge.plantumldependency.commoncli.command.CommandLine;
import net.sourceforge.plantumldependency.commoncli.command.impl.CommandLineImpl;
import net.sourceforge.plantumldependency.commoncli.exception.CommandLineException;
import net.sourceforge.plantumldependency.commoncli.program.JavaProgram;
import net.sourceforge.plantumldependency.commoncli.program.execution.JavaProgramExecution;

public class PlantUML {

	private static final String encoding = "UTF-8";
	private static final String objectFilter = "abstract_classes,classes,extensions,implementations,imports,interfaces,native_methods,static_imports";

	/**
	 * Convenience method for generating plantUML for a project on a SCM repo
	 *
	 * @param scmServer
	 *            git client using to clone repo
	 * @param localDirectory
	 *            where the repo should be cloned
	 * @param projectKey
	 *            stash's project key
	 * @param repositorySlug
	 *            stash's repo slug
	 * @param branchName
	 *            the name of the branch that is to be cloned
	 * @param packageFilter
	 *            regular expression to filtering package which will show on
	 *            diagrams
	 * @param diagramFilter
	 *            the names of object which will be showing on diagram ( def.
	 *            abstract_classes,classes,extensions,implementations,
	 *            imports,interfaces,native_methods,static_imports_
	 * @param fileFormat
	 *            the name of file extension
	 * @return content of image file or plant UML description when not
	 *         specifying @see fileFormat
	 * @throws PlantUMLException
	 */

	@Nonnull
	public static String fromRepo(@Nonnull SCM scmClient, @Nonnull String localDirectoryPath,
			@Nonnull String projectKey, @Nonnull String repositorySlug, @Nonnull String branchName,
			@Nullable String packageFilter, @Nullable String diagramFilter, @Nullable FileFormat fileformat)
					throws PlantUMLException {

		Preconditions.checkNotNull(scmClient);
		Preconditions.checkNotNull(localDirectoryPath);
		Preconditions.checkNotNull(repositorySlug);
		Preconditions.checkNotNull(branchName);

		File localDirectory = new File(localDirectoryPath);
		PlantUML plantUML = new PlantUML();
		createDirectoryIfNecessary(Paths.get(localDirectory.toString()));
		cloneTheRepo(scmClient, projectKey, repositorySlug, branchName, Paths.get(localDirectory.toString()));
		String plantUMLDescription = plantUML.generateUmlDescription(localDirectory, packageFilter, diagramFilter);
		return (fileformat != null) ? plantUML.generateImage(plantUMLDescription, fileformat) : plantUMLDescription;
	}

	/**
	 * Convenience method for generating plantUML for a project on fileDirectory
	 *
	 * @param localDirectoryPath
	 *            path to directory where project has been cloned
	 * @param packageFilter
	 *            regular expression to filtering package which will show on
	 *            diagrams
	 * @param diagramFilter
	 *            the names of object which will be showing on diagram ( def.
	 *            abstract_classes,classes,extensions,implementations,
	 *            imports,interfaces,native_methods,static_imports_
	 * @param fileFormat
	 *            the name of file extension
	 * @return content of image file or plant UML description when not
	 *         specifying @see fileFormat
	 * @throws PlantUMLException
	 */

	@Nonnull
	public static String fromDirectory(@Nonnull String localDirectoryPath, @Nullable String packageFilter,
			@Nullable String diagramFilter, @Nullable FileFormat fileformat) throws PlantUMLException {

		Preconditions.checkNotNull(localDirectoryPath);
		File localDirectory = new File(localDirectoryPath);
		PlantUML plantUML = new PlantUML();
		createDirectoryIfNecessary(Paths.get(localDirectory.toString()));
		String plantUMLDescription = plantUML.generateUmlDescription(localDirectory, packageFilter, diagramFilter);
		return (fileformat != null) ? plantUML.generateImage(plantUMLDescription, fileformat) : plantUMLDescription;

	}

	private String generateUmlDescription(@Nonnull File directory, @Nullable String packageFilter,
			@Nullable String diagramFilter) throws PlantUMLException {

		Preconditions.checkNotNull(directory);
		String svg = "";
		JavaProgramExecution plantumlDependencyProgramExecution;
		JavaProgram plantumlDependencyProgram;

		try {

			File tempFile = File.createTempFile("prefix-", "-suffix");
			tempFile.deleteOnExit();

			final CommandLine commandLineArguments = new CommandLineImpl(
					new String[] { "-o", tempFile.getAbsolutePath(), "-b", directory.getAbsolutePath(), "-dt",
							Strings.isNullOrEmpty(diagramFilter) ? objectFilter : diagramFilter, "-dp",
							Strings.isNullOrEmpty(packageFilter) ? "*.*" : packageFilter });
			plantumlDependencyProgram = new PlantUMLDependencyProgram();
			plantumlDependencyProgramExecution = plantumlDependencyProgram.parseCommandLine(commandLineArguments);
			plantumlDependencyProgramExecution.execute();
			svg = new String(Files.readAllBytes(Paths.get(tempFile.getAbsolutePath())), encoding);

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
		return svg;
	}

	private String generateImage(@Nonnull String plantUMLDescription, @Nonnull FileFormat fileformat)
			throws PlantUMLException {

		String svg = "";
		Preconditions.checkNotNull(plantUMLDescription);

		SourceStringReader reader = new SourceStringReader(plantUMLDescription);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {

			reader.generateImage(os, new FileFormatOption(fileformat));
			os.close();
			svg = new String(os.toByteArray(), Charset.forName(encoding));
		} catch (IOException e) {
			throw new PlantUMLException("general I/O exception", e);
		}

		return svg;

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
