package com.networkedassets.autodoc.transformer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.networkedassets.autodoc.transformer.settings.TestObject;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager made for testing purposes only
 */
public class TestManager {

    public static int counter = 0;

    private TestObject testObject = new TestObject();

    public TestManager(){
        counter++;
        testObject.setString("COUNTER: " + counter);
        Map<String, String> map = new HashMap<>();
        map.put("jeden", "1");
        map.put("dwa", "2");
        map.put("trzy", "3");
        testObject.setMap(map);
        testObject.setList(ImmutableList.<String>of("uno", "dos", "tres"));
    }

    public TestObject getTestObject(){
        return testObject;
    }

    public void setTestObject(TestObject testObject){
        this.testObject = testObject;
    }

}
