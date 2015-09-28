package com.networkedassets.autodoc.transformer.javadoc;

import com.google.common.base.Joiner;
import com.networkedassets.autodoc.transformer.clients.git.api.SCM;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private List<File> sourceFiles;
    private File javadocDirectory;
    private String javadocExecutablePath = "javadoc";

    /**
     * Main constructor
     * @param javadocDirectory directory where the docs are going to be located
     * @param sourceFiles source files for docs generation
     */
    public Javadoc(File javadocDirectory, List<File> sourceFiles) {
        this.sourceFiles = sourceFiles;
        this.javadocDirectory = javadocDirectory;
    }

    /**
     * Constructor with empty file list
     * @param javadocDirectory directory where the docs are going to be located
     */
    public Javadoc(File javadocDirectory) {
        this.javadocDirectory = javadocDirectory;
        this.sourceFiles = new ArrayList<>();
    }

    /**
     * Convinience method for generating javadoc for a project on a SCM repo
     * @param scmServer server given project is located on
     * @param localDirectoryPath where the repo should be cloned
     * @param projectKey stash's project key
     * @param repositorySlug stash's repo slug
     * @param branchName the name of the branch that is to be cloned
     * @throws JavadocException
     * @return the location of the javadoc (subdirectory of the localDirectoryPath)
     */
    @Nonnull
    public static String fromStashRepo(@Nonnull SCM scmServer, @Nonnull String localDirectoryPath,
                                     @Nonnull String projectKey, @Nonnull String repositorySlug,
                                     @Nonnull String branchName) throws JavadocException {
        File localDirectory = new File(localDirectoryPath);
        createDirectoryIfNecessary(localDirectory);
        cloneTheRepo(scmServer, projectKey, repositorySlug, branchName, localDirectory);
        return Javadoc.fromDirectory(localDirectory);
    }

    /**
     * Convenience method for generating javadoc for a directory and it's subdirectories recursively
     * @param localDirectory the directory javadoc should be generated for
     * @return the location of the javadoc (subdirectory of the localDirectory)
     * @throws JavadocException
     */
    @Nonnull
    public static String fromDirectory(@Nonnull File localDirectory) throws JavadocException {
        System.out.println("From directory: " + localDirectory.getAbsolutePath());
        Javadoc javadoc = new Javadoc(new File(localDirectory, "javadoc"));
        List<File> javaFiles = searchJavaFiles(localDirectory);
        System.out.println("Java files:\n" + Joiner.on("\n").join(javaFiles));
        javadoc.addFiles(javaFiles);
        javadoc.generate();
        System.out.println("Javadoc generated");
        return javadoc.javadocDirectory.getAbsolutePath();
    }

    @Nonnull
    public static List<File> searchJavaFiles(@Nonnull File localDirectory) throws JavadocException {
        try {
            return Files.walk(localDirectory.toPath(), FileVisitOption.FOLLOW_LINKS)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new JavadocException("Could not search files", e);
        }
    }

    private static void cloneTheRepo(@Nonnull SCM scmServer, @Nonnull String projectKey, @Nonnull String repositorySlug,
                                     @Nonnull String branchName, File localDirectory) throws JavadocException {
        try {
            scmServer.cloneRepository(localDirectory, projectKey, repositorySlug, branchName);
        } catch (Exception e) {
            throw new JavadocException("could not clone the repo", e);
        }
    }

    private static void createDirectoryIfNecessary(@Nonnull File localDirectory) throws JavadocException {
        if (!(localDirectory.isDirectory() || localDirectory.mkdirs())) {
            throw new JavadocException("could not create the directory: " + localDirectory.getPath());
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
     * @param javadocExecutablePath path to the executable
     */
    public void setJavadocExecutablePath(String javadocExecutablePath) {
        this.javadocExecutablePath = javadocExecutablePath;
    }

    /**
     * Generates the javadoc
     * @throws JavadocException
     */
    public void generate() throws JavadocException {
        Stream<String> args = Stream.concat(
                Stream.of(
                        javadocExecutablePath, // javadoc
                        "-d", javadocDirectory.getAbsolutePath()),  // -d ./javadoc/
                sourceFiles.stream().map(File::getAbsolutePath)); // ./src/com/example/File1.java ./src/com/example/File2.java

        ProcessBuilder javadocProcessBuilder = new ProcessBuilder(args.toArray(String[]::new)).redirectErrorStream(true);

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
     * @param javaFiles collection of files to be added
     */
    public void addFiles(Iterable<File> javaFiles) {
        for (File f : javaFiles) {
            addFile(f);
        }
    }

    /**
     * Adds the file to the list of java source files used to generate javadoc
     * @param javaFile file that should be added
     */
    public void addFile(File javaFile) {
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
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}


