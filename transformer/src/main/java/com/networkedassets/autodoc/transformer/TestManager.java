package com.networkedassets.autodoc.transformer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.networkedassets.autodoc.transformer.settings.TestObject;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

/**
 * Created by kamil on 22.09.2015.
 */
@Resource
@ManagedBean
public class TestManager {

    public static int counter = 0;

    private TestObject testObject = new TestObject();

    public TestManager(){
        counter++;
        testObject.setString("COUNTER: " + counter);
        testObject.setMap(ImmutableMap.<String, String>of("he", "ha", "hi", "ho"));
        testObject.setList(ImmutableList.<String>of("uno", "dos", "tres"));
    }

    public TestObject getTestObject(){
        return testObject;
    }

    public void setTestObject(TestObject testObject){
        this.testObject = testObject;
    }

}
