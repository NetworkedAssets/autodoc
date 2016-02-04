package com.networkedassets.autodoc.documentation.data;

/**
 * Created by mtulaza on 2016-02-02.
 */
public class Entity {
    private String packageName;
    private String docPieceName;
    private String JSONdata;

    public Entity() {
    }

    public Entity(String packageName, String docPieceName, String JSONdata) {
        this.packageName = packageName;
        this.docPieceName = docPieceName;
        this.JSONdata = JSONdata;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDocPieceName() {
        return docPieceName;
    }

    public void setDocPieceName(String docPieceName) {
        this.docPieceName = docPieceName;
    }

    public String getJSONdata() {
        return JSONdata;
    }

    public void setJSONdata(String JSONdata) {
        this.JSONdata = JSONdata;
    }
}