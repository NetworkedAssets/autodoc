package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.xjc.Root;
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

    @SuppressWarnings("Duplicates")
    public static Root executeJavadoc(Class<?> docletClass, String extendedClassPath, List<String> sourcePaths, List<String> packages, List<String> sourceFiles,
                                      List<String> subPackages, String... additionalArguments) {
        try {
            OutputStream errors = new LoggingOutputStream(log, LoggingLevelEnum.ERROR);
            OutputStream warnings = new LoggingOutputStream(log, LoggingLevelEnum.WARN);
            OutputStream notices = new LoggingOutputStream(log, LoggingLevelEnum.INFO);

            PrintWriter errorWriter = new PrintWriter(errors, false);
            PrintWriter warningWriter = new PrintWriter(warnings, false);
            PrintWriter noticeWriter = new PrintWriter(notices, false);

            // aggregate arguments and packages
            ArrayList<String> argumentList = new ArrayList<String>();

            // by setting this to 'private', nothing is omitted in the parsing
            argumentList.add("-private");

            String classPath = System.getProperty("java.class.path", ".");
            if (extendedClassPath != null) {
                classPath += File.pathSeparator + extendedClassPath;
            }
            argumentList.add("-classpath");
            argumentList.add(classPath);

            if (sourcePaths != null) {
                String concatenatedSourcePaths = join(File.pathSeparator, sourcePaths);
                if (concatenatedSourcePaths.length() > 0) {
                    argumentList.add("-sourcepath");
                    argumentList.add(concatenatedSourcePaths);
                }
            }

            if (subPackages != null) {
                String concatenatedSubPackages = join(";", subPackages);
                if (concatenatedSubPackages.length() > 0) {
                    argumentList.add("-subpackages");
                    argumentList.add(concatenatedSubPackages);
                }
            }

            if (packages != null) {
                argumentList.addAll(packages);
            }

            if (sourceFiles != null) {
                argumentList.addAll(sourceFiles);
            }

            if (additionalArguments != null) {
                argumentList.addAll(Arrays.asList(additionalArguments));
            }

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

        return XmlDoclet.root;
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

    public static String join(String glue, String[] strings) {
        return join(glue, Arrays.asList(strings));
    }
}
