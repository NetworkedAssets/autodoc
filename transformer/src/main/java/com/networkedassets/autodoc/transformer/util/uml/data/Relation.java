package com.networkedassets.autodoc.transformer.util.uml.data;

/**
 * Created by mtulaza on 2016-02-02.
 */
public class Relation {
    private String source;
    private String type;
    private String target;

    public Relation() {
    }

    public Relation(String source, String type, String target) {
        this.source = source;
        this.type = type;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
