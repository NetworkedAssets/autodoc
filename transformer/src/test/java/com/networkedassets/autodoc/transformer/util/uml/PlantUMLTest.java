package com.networkedassets.autodoc.transformer.util.uml;

import net.sourceforge.plantumldependency.commoncli.exception.CommandLineException;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PlantUMLTest{

    @Test
    public void testFromDirectory(){
        Path path = Paths.get(System.getProperty("user.dir"));
        Path result = null;
        try{
            result = PlantUML.fromDirectory(path,null,null);
        }  catch(PlantUMLException p){
            Assert.fail("PlantUMLException: " + p.getMessage());
        }

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getFileName().toString().matches("prefix-\\d+-suffix"));
    }

    @Test(expected = CommandLineException.class)
    public void throwsCommandLineException() throws Throwable{
        try {
            PlantUML.fromDirectory(Paths.get("default"), null, null);
        } catch(Exception e){
           throw e.getCause();
        }
    }

    @Test(expected = PlantUMLException.class)
    public void throwsPlantUMLException() throws Throwable{
        Path path = Paths.get(System.getProperty("user.dir"));
        PlantUML.fromDirectory(path, "abc", null);
    }
}
