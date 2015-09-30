package com.networkedassets.autodoc.transformer.uml;

import com.google.common.base.Preconditions;
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

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

public class PlantUML {

    private static final String encoding = "UTF-8";

    /**
     * Convinience method for generating plant uml description  for a project on a SCM repo
     *
     * @param scmServer          server given project is located on
     * @param localDirectoryPath where the repo should be cloned
     * @param projectKey         stash's project key
     * @param repositorySlug     stash's repo slug
     * @param branchName         the name of the branch that is to be cloned
     * @return plant uml description
     * @throws PlantUMLException
     */

    @Nonnull
    public static String fromRepoAsText(@Nonnull SCM scmServer, @Nonnull String localDirectoryPath,
                                        @Nonnull String projectKey, @Nonnull String repositorySlug, @Nonnull String branchName)
            throws PlantUMLException {

        Preconditions.checkNotNull(scmServer);
        Preconditions.checkNotNull(localDirectoryPath);
        Preconditions.checkNotNull(repositorySlug);
        Preconditions.checkNotNull(branchName);

        Path localDirectory = Paths.get(localDirectoryPath);
        PlantUML plantUML = new PlantUML();
        createDirectoryIfNecessary(localDirectory);
        cloneTheRepo(scmServer, projectKey, repositorySlug, branchName, localDirectory);
        return plantUML.generateAsText(localDirectory);
    }


    /**
     * Convinience method for generating plant uml in svg format for a project on a SCM repo
     *
     * @param scmClient          client using to clone repo
     * @param localDirectoryPath where the repo should be cloned
     * @param projectKey         stash's project key
     * @param repositorySlug     stash's repo slug
     * @param branchName         the name of the branch that is to be cloned
     * @return plant uml in svg format
     * @throws PlantUMLException
     */


    @Nonnull
    public static String fromRepoAsSVG(@Nonnull SCM scmClient, @Nonnull String localDirectoryPath,
                                       @Nonnull String projectKey, @Nonnull String repositorySlug, @Nonnull String branchName)
            throws PlantUMLException {

        Preconditions.checkNotNull(scmClient);
        Preconditions.checkNotNull(localDirectoryPath);
        Preconditions.checkNotNull(repositorySlug);
        Preconditions.checkNotNull(branchName);

        Path localDirectory = Paths.get(localDirectoryPath);
        PlantUML plantUML = new PlantUML();
        createDirectoryIfNecessary(localDirectory);
        cloneTheRepo(scmClient, projectKey, repositorySlug, branchName, localDirectory);
        String plantUMLDescription = plantUML.generateAsText(localDirectory);
        return plantUML.generateAsSVG(plantUMLDescription);
    }

    private static void cloneTheRepo(@Nonnull SCM scmClient, @Nonnull String projectKey,
                                     @Nonnull String repositorySlug, @Nonnull String branchName, @Nonnull Path localDirectory)
            throws PlantUMLException {
        try {

            Preconditions.checkNotNull(scmClient);
            Preconditions.checkNotNull(projectKey);
            Preconditions.checkNotNull(repositorySlug);
            Preconditions.checkNotNull(branchName);
            Preconditions.checkNotNull(localDirectory);

            scmClient.cloneRepository(localDirectory, projectKey, repositorySlug, branchName);
        } catch (Exception e) {
            throw new PlantUMLException("could not clone the repo", e);
        }
    }

    private static void createDirectoryIfNecessary(@Nonnull Path localDirectory)
            throws PlantUMLException {
        try {
            if (!Files.isDirectory(localDirectory)) {
                Files.createDirectories(localDirectory);
            }
        } catch (IOException e) {
            throw new PlantUMLException("could not create the directory: " + localDirectory, e);
        }
    }

    private String generateAsText(@Nonnull Path directory) throws PlantUMLException {

        Preconditions.checkNotNull(directory);
        String svg = "";
        JavaProgramExecution plantumlDependencyProgramExecution;
        JavaProgram plantumlDependencyProgram;

        try {

            File tempFile = File.createTempFile("prefix-", "-suffix");
            tempFile.deleteOnExit();

            final CommandLine commandLineArguments = new CommandLineImpl(
                    new String[]{"-o", tempFile.getAbsolutePath(), "-b", directory.toAbsolutePath().toString(), "-e",
                            "**/package-info.java"});
            plantumlDependencyProgram = new PlantUMLDependencyProgram();
            plantumlDependencyProgramExecution = plantumlDependencyProgram
                    .parseCommandLine(commandLineArguments);
            plantumlDependencyProgramExecution.execute();
            svg = new String(Files.readAllBytes(Paths.get(tempFile.getAbsolutePath())), encoding);
        } catch (MalformedURLException e) {
            throw new PlantUMLException("URL string is not parseable or contains an unsupported protocol",
                    e);
        } catch (CommandLineException e) {
            throw new PlantUMLException("wrong commandline arguments", e);
        } catch (ParseException e) {
            throw new PlantUMLException("can't parse commandline arguments", e);
        } catch (IOException e) {
            throw new PlantUMLException("general I/O exception", e);
        }
        return svg;
    }

    private String generateAsSVG(@Nonnull String plantUMLDescription) throws PlantUMLException {

        String svg = "";
        Preconditions.checkNotNull(plantUMLDescription);

        SourceStringReader reader = new SourceStringReader(plantUMLDescription);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
            os.close();
            svg = new String(os.toByteArray(), Charset.forName(encoding));
        } catch (IOException e) {
            throw new PlantUMLException("general I/O exception", e);
        }

        return svg;

    }
}
