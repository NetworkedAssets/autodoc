package com.networkedassets.autodoc.transformer.util.javadoc;

import com.networkedassets.autodoc.jsondoclet.JavadocRunner;
import com.networkedassets.autodoc.jsondoclet.JsonDoclet;
import com.networkedassets.autodoc.jsondoclet.model.Root;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
     * @throws JavadocException
     */

    @Nonnull
    public static Root structureFromDirectory(@Nonnull Path localDirectory) throws JavadocException {
        Path javadocPath = localDirectory.resolve("javadoc");
        Javadoc javadoc = new Javadoc(javadocPath);
        List<Path> javaFiles = searchJavaFiles(localDirectory);
        javadoc.addFiles(javaFiles);
        return javadoc.generate(JsonDoclet.class);
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

     * @param docletClass
     * @throws JavadocException
     */
    public Root generate(Class<?> docletClass) throws JavadocException {
        return JavadocRunner.executeJavadoc(docletClass, null, null, null,
                sourceFiles.stream().map(Path::toString).collect(Collectors.toList()), null);
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

}
