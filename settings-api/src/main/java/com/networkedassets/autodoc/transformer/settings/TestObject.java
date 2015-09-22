package com.networkedassets.autodoc.transformer.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * POJO made for testing purposes only
 */
public class TestObject {
    private String noSetterString = "";
    private String string = "";
    private Map<String, String> map = new HashMap<>();
    private List<String> list = new ArrayList<>();


    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String getNoSetterString() {
        return noSetterString;
    }


}
