package com.networkedassets.autodoc.documentation.data;

/**
 * Created by mtulaza on 2016-02-02.
 */
public class Entity {
    private String packageName;
    private String fqcn;
    private String JSONdata;

    public Entity() {
    }

    public Entity(String packageName, String fqcn, String JSONdata) {
        this.packageName = packageName;
        this.fqcn = fqcn;
        this.JSONdata = JSONdata;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFqcn() {
        return fqcn;
    }

    public void setFqcn(String fqcn) {
        this.fqcn = fqcn;
    }

    public String getJSONdata() {
        return JSONdata;
    }

    public void setJSONdata(String JSONdata) {
        this.JSONdata = JSONdata;
    }
}