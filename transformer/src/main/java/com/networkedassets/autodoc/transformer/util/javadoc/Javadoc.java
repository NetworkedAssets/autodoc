package com.networkedassets.autodoc.transformer.util.javadoc;

import com.github.markusbernhardt.xmldoclet.JavadocRunner;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Javadoc wrapper for generating javadoc pages TODO: Change all the printlns to
 * proper logging
 */
public class Javadoc {
    private List<Path> sourceFiles;
    private Path javadocDirectory;
    private String javadocExecutablePath = "javadoc";

    /**
     * Main constructor
     *
     * @param javadocDirectory directory where the docs are going to be located
     * @param sourceFiles      source files for docs generation
     */
    public Javadoc(Path javadocDirectory, List<Path> sourceFiles) {
        this.sourceFiles = sourceFiles;
        this.javadocDirectory = javadocDirectory;
    }

    /**
     * Constructor with empty file list
     *
     * @param javadocDirectory directory where the docs are going to be located
     */
    public Javadoc(Path javadocDirectory) {
        this.javadocDirectory = javadocDirectory;
        this.sourceFiles = new ArrayList<>();
    }

    /**
     * Convenience method for generating javadoc for a directory and it's
     * subdirectories recursively
     *
     * @param localDirectory the directory javadoc should be generated for
     * @return the location of the javadoc (subdirectory of the localDirectory)
     * @throws JavadocException
     */
    @Nonnull
    public static Path fromDirectory(@Nonnull Path localDirectory, @Nullable String docletPath,
                                     @Nullable String docletClass) throws JavadocException {
        System.out.println("From directory: " + localDirectory.toAbsolutePath());
        Javadoc javadoc = new Javadoc(Paths.get(localDirectory.toString(), "javadoc"));
        List<Path> javaFiles = searchJavaFiles(localDirectory);
        System.out.println("Java files:\n" + Joiner.on("\n").join(javaFiles));
        javadoc.addFiles(javaFiles);
        javadoc.generate(docletPath, docletClass);
        System.out.println("Javadoc generated");
        return javadoc.javadocDirectory;
    }

    @Nonnull
    public static List<Path> searchJavaFiles(@Nonnull Path localDirectory) throws JavadocException {
        try (Stream<Path> javaFiles = Files.walk(localDirectory, FileVisitOption.FOLLOW_LINKS)
                .filter(p -> p.toString().endsWith(".java"))) {
            return javaFiles.collect(Collectors.toList());
        } catch (IOException e) {
            throw new JavadocException("Could not search files", e);
        }
    }

    private static void createDirectoryIfNecessary(@Nonnull Path localDirectory) throws JavadocException {
        try {
            if (!Files.isDirectory(localDirectory)) {
                Files.createDirectories(localDirectory);
            }
        } catch (IOException e) {
            throw new JavadocException("could not create the directory: " + localDirectory, e);
        }
    }

    /**
     * @return the command that runs javadoc. Default is "javadoc" - the javadoc
     * currently in system's PATH
     */
    public String getJavadocExecutablePath() {
        return javadocExecutablePath;
    }

    /**
     * Sets the command that should be run in place of javadoc
     *
     * @param javadocExecutablePath path to the executable
     */
    public void setJavadocExecutablePath(String javadocExecutablePath) {
        this.javadocExecutablePath = javadocExecutablePath;
    }

    /**
     * Generates the javadoc
     *
     * @param docletPath  the path for doclet jar
     * @param docletClass
     * @throws JavadocException
     */
    public void generate(@Nullable String docletPath, @Nullable String docletClass) throws JavadocException {
        createDirectoryIfNecessary(javadocDirectory);
        Stream<String> args = Strings.isNullOrEmpty(docletPath)
                ? Stream.concat(Stream.of(javadocExecutablePath, "-d", javadocDirectory.toString()),
                sourceFiles.stream().map(Path::toString))
                : Stream.concat(Stream.of(javadocExecutablePath, "-doclet", docletClass, "-docletpath", docletPath, // javadoc
                "-d", javadocDirectory.toString()), sourceFiles.stream().map(Path::toString));

        String[] command = args.toArray(String[]::new);
        System.out.println("Args: " + Joiner.on(" ").join(command));

        ProcessBuilder javadocProcessBuilder = new ProcessBuilder(command)
                .redirectErrorStream(true);

        System.out.println("Generating javadoc...");
        StreamGobbler gobbler;
        try {
            Process javadocProcess = javadocProcessBuilder.start();
            gobbler = new StreamGobbler(javadocProcess.getInputStream(), "");
            gobbler.start();
            if (!javadocProcess.waitFor(5, TimeUnit.MINUTES)) {
                throw new JavadocException("javadoc process error");
            }
        } catch (InterruptedException | IOException e) {
            throw new JavadocException(e);
        }
        System.out.println("Javadoc successfully generated");

    }

    public Root generate(Class<?> docletClass) throws JavadocException {
        createDirectoryIfNecessary(javadocDirectory);
        return JavadocRunner.executeJavadoc(docletClass, null, null, null,
                sourceFiles.stream().map(Path::toString).collect(Collectors.toList()), null,
                "-d", javadocDirectory.toString());
    }

    /**
     * Generates the javadoc with custom doclet
     *
     * @param docletPath
     *            the path for doclet jar
     * @param docletClass
     *
     * @throws JavadocException
     */

    /**
     * Adds the files that should be looked at during javadoc's generation
     *
     * @param javaFiles collection of files to be added
     */
    public void addFiles(Iterable<Path> javaFiles) {
        for (Path f : javaFiles) {
            addFile(f);
        }
    }

    /**
     * Adds the file to the list of java source files used to generate javadoc
     *
     * @param javaFile file that should be added
     */
    public void addFile(Path javaFile) {
        sourceFiles.add(javaFile);
    }

    private class StreamGobbler extends Thread {
        InputStream is;
        String type;

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null)
                    System.out.println(type + "> " + line);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
