package com.networkedassets.autodoc.jsondoclet;

import com.networkedassets.autodoc.jsondoclet.model.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavadocRunner {
    private static Logger log = LoggerFactory.getLogger(JavadocRunner.class);

    public static Root executeJavadoc(Class<?> docletClass, String extendedClassPath, List<String> sourcePaths,
                                      List<String> packages, List<String> sourceFiles, List<String> subPackages,
                                      String... additionalArguments) {
        try {
            OutputStream errors = new LoggingOutputStream(log, LoggingLevelEnum.ERROR);
            OutputStream warnings = new LoggingOutputStream(log, LoggingLevelEnum.WARN);
            OutputStream notices = new LoggingOutputStream(log, LoggingLevelEnum.INFO);

            PrintWriter errorWriter = new PrintWriter(errors, false);
            PrintWriter warningWriter = new PrintWriter(warnings, false);
            PrintWriter noticeWriter = new PrintWriter(notices, false);

            ArrayList<String> argumentList =
                    createArgumentList(extendedClassPath, sourcePaths, packages, sourceFiles, subPackages, additionalArguments);

            log.info("Executing doclet with arguments: " + join(" ", argumentList));

            String[] arguments = argumentList.toArray(new String[argumentList.size()]);
            com.sun.tools.javadoc.Main.execute("xml-doclet", errorWriter, warningWriter, noticeWriter,
                    docletClass.getName(), arguments);

            errors.close();
            warnings.close();
            notices.close();

            log.info("done with doclet processing");
        } catch (Exception e) {
            log.error("doclet exception", e);
        } catch (Error e) {
            log.error("doclet error", e);
        }

        return JsonDoclet.root;
    }

    private static ArrayList<String> createArgumentList(String extendedClassPath, List<String> sourcePaths,
                                                        List<String> packages, List<String> sourceFiles,
                                                        List<String> subPackages, String[] additionalArguments) {
        ArrayList<String> argumentList = new ArrayList<>();

        argumentList.add("-private");

        addClasspathToArgumentList(argumentList, extendedClassPath);
        addListToArgumentList(argumentList, "-sourcepath", sourcePaths, File.pathSeparator);
        addListToArgumentList(argumentList, "-subpackages", subPackages, ";");
        addToArgumentList(argumentList, packages);
        addToArgumentList(argumentList, sourceFiles);
        addAdditionalArgsToArgumentList(argumentList, additionalArguments);
        return argumentList;
    }

    private static void addAdditionalArgsToArgumentList(ArrayList<String> argumentList, String[] additionalArguments) {
        if (additionalArguments != null) {
            argumentList.addAll(Arrays.asList(additionalArguments));
        }
    }

    private static void addToArgumentList(ArrayList<String> argumentList, List<String> packages) {
        if (packages != null) {
            argumentList.addAll(packages);
        }
    }

    private static void addListToArgumentList(ArrayList<String> argumentList, String optName, List<String> listToAdd,
                                              String pathSeparator) {
        if (listToAdd != null) {
            String concatenatedSourcePaths = join(pathSeparator, listToAdd);
            if (concatenatedSourcePaths.length() > 0) {
                argumentList.add(optName);
                argumentList.add(concatenatedSourcePaths);
            }
        }
    }

    private static void addClasspathToArgumentList(ArrayList<String> argumentList, String extendedClassPath) {
        String classPath = System.getProperty("java.class.path", ".");
        if (extendedClassPath != null) {
            classPath += File.pathSeparator + extendedClassPath;
        }
        argumentList.add("-classpath");
        argumentList.add(classPath);
    }

    public static String join(String glue, Iterable<String> strings) {
        if (strings == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        String glu = "";
        for (String string : strings) {
            stringBuilder.append(glu);
            stringBuilder.append(string);
            glu = glue;
        }
        return stringBuilder.toString();
    }
}
