package com.networkedassets.autodoc.transformer.util.uml;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantumldependency.cli.main.program.PlantUMLDependencyProgram;
import net.sourceforge.plantumldependency.commoncli.command.CommandLine;
import net.sourceforge.plantumldependency.commoncli.command.impl.CommandLineImpl;
import net.sourceforge.plantumldependency.commoncli.exception.CommandLineException;
import net.sourceforge.plantumldependency.commoncli.program.JavaProgram;
import net.sourceforge.plantumldependency.commoncli.program.execution.JavaProgramExecution;

public class PlantUML {

	private static final String objectFilter = "abstract_classes,classes,extensions,implementations,imports,interfaces,native_methods,static_imports";

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
	public static Path fromDirectory(@Nonnull Path localDirectory, @Nullable String diagramFilter,
			@Nullable FileFormat fileFormat) throws PlantUMLException {

		Preconditions.checkNotNull(localDirectory);
		PlantUML plantUML = new PlantUML();
		return plantUML.generateUmlDescription(localDirectory, diagramFilter);

	}

	private Path generateUmlDescription(@Nonnull Path localDirectory, @Nullable String diagramFilter)
			throws PlantUMLException {

		Preconditions.checkNotNull(localDirectory);
		Path results = null;
		JavaProgramExecution plantumlDependencyProgramExecution;
		JavaProgram plantumlDependencyProgram;

		try {

			File tempFile = File.createTempFile("prefix-", "-suffix");

			final CommandLine commandLineArguments = new CommandLineImpl(
					new String[] { "-o", tempFile.getAbsolutePath(), "-b", localDirectory.toFile().getAbsolutePath(),
							"-dt", Strings.isNullOrEmpty(diagramFilter) ? objectFilter : diagramFilter });
			plantumlDependencyProgram = new PlantUMLDependencyProgram();
			plantumlDependencyProgramExecution = plantumlDependencyProgram.parseCommandLine(commandLineArguments);
			plantumlDependencyProgramExecution.execute();
			results = Paths.get(tempFile.getAbsolutePath());

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

}
