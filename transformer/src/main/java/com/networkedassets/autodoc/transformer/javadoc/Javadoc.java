package com.networkedassets.autodoc.transformer.javadoc;

import com.google.common.base.Joiner;
import com.networkedassets.autodoc.transformer.clients.git.api.SCM;

import javax.annotation.Nonnull;
import java.io.*;
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
 * Javadoc wrapper for generating javadoc pages
 * TODO: Change all the printlns to proper logging
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
     * Convinience method for generating javadoc for a project on a SCM repo
     *
     * @param scmServer      server given project is located on
     * @param localDirectory where the repo should be cloned
     * @param projectKey     stash's project key
     * @param repositorySlug stash's repo slug
     * @param branchName     the name of the branch that is to be cloned
     * @return the location of the javadoc (subdirectory of the localDirectory)
     * @throws JavadocException
     */
    @Nonnull
    public static Path fromStashRepo(@Nonnull SCM scmServer, @Nonnull Path localDirectory,
                                     @Nonnull String projectKey, @Nonnull String repositorySlug,
                                     @Nonnull String branchName) throws JavadocException {
        createDirectoryIfNecessary(localDirectory);
        cloneTheRepo(scmServer, projectKey, repositorySlug, branchName, localDirectory);
        return Javadoc.fromDirectory(localDirectory);
    }

    /**
     * Convenience method for generating javadoc for a directory and it's subdirectories recursively
     *
     * @param localDirectory the directory javadoc should be generated for
     * @return the location of the javadoc (subdirectory of the localDirectory)
     * @throws JavadocException
     */
    @Nonnull
    public static Path fromDirectory(@Nonnull Path localDirectory) throws JavadocException {
        System.out.println("From directory: " + localDirectory.toAbsolutePath());
        Javadoc javadoc = new Javadoc(Paths.get(localDirectory.toString(), "javadoc"));
        List<Path> javaFiles = searchJavaFiles(localDirectory);
        System.out.println("Java files:\n" + Joiner.on("\n").join(javaFiles));
        javadoc.addFiles(javaFiles);
        javadoc.generate();
        System.out.println("Javadoc generated");
        return javadoc.javadocDirectory;
    }

    @Nonnull
    public static List<Path> searchJavaFiles(@Nonnull Path localDirectory) throws JavadocException {
        try (Stream<Path> javaFiles = Files.walk(localDirectory, FileVisitOption.FOLLOW_LINKS)
                .filter(p -> p.endsWith(".java"))) {
            return javaFiles.collect(Collectors.toList());
        } catch (IOException e) {
            throw new JavadocException("Could not search files", e);
        }
    }

    private static void cloneTheRepo(@Nonnull SCM scmServer, @Nonnull String projectKey, @Nonnull String repositorySlug,
                                     @Nonnull String branchName, Path localDirectory) throws JavadocException {
        try {
            scmServer.cloneRepository(localDirectory, projectKey, repositorySlug, branchName);
        } catch (Exception e) {
            throw new JavadocException("could not clone the repo", e);
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
     * @return the command that runs javadoc. Default is "javadoc" - the javadoc currently in system's PATH
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
     * @throws JavadocException
     */
    public void generate() throws JavadocException {
        Stream<String> args = Stream.concat(
                Stream.of(
                        javadocExecutablePath, // javadoc
                        "-d", javadocDirectory.toString()),  // -d ./javadoc/
                sourceFiles.stream().map(Path::toString)); // ./src/com/example/File1.java ./src/com/example/File2.java

        ProcessBuilder javadocProcessBuilder = new ProcessBuilder(args.toArray(String[]::new))
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


