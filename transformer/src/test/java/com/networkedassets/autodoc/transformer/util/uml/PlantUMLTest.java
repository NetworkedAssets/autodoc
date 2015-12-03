package com.networkedassets.autodoc.transformer.util.uml;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantumldependency.commoncli.exception.CommandLineException;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PlantUMLTest{

    @Test
    public void testFromDirectoryCreatesNewFile() throws PlantUMLException{
        Path path = Paths.get(System.getProperty("user.dir"));
        Path result = PlantUML.fromDirectory(path,null,null);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.toFile().exists());
        Assert.assertTrue(result.toFile().isFile());
    }

    @Test
    public void testFromDirectoryWithFileFormatCreatesNewFile() throws PlantUMLException{
        Path path = Paths.get(System.getProperty("user.dir"));
        Path result = PlantUML.fromDirectory(path,null,FileFormat.HTML5);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.toFile().exists());
        Assert.assertTrue(result.toFile().isFile());
    }

    @Test(expected = CommandLineException.class)
    public void testFromDirectoryGivenWrongPathThrowsCommandLineException() throws Throwable{
        try {
            PlantUML.fromDirectory(Paths.get("default"), null, null);
        } catch(Exception e){
           throw e.getCause();
        }
    }

    @Test(expected = PlantUMLException.class)
    public void testFromDirectoryGivenWrongFilterThrowsPlantUMLException() throws Throwable{
        Path path = Paths.get(System.getProperty("user.dir"));
        PlantUML.fromDirectory(path, "abc", null);
    }

    @Test
    public void testFromDirectoryGivenCorrectFilterReturnsNewFile() throws PlantUMLException{
        Path path = Paths.get(System.getProperty("user.dir"));
        Path result = PlantUML.fromDirectory(path,"classes,interfaces",null);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.toFile().exists());
        Assert.assertTrue(result.toFile().isFile());
    }
}
