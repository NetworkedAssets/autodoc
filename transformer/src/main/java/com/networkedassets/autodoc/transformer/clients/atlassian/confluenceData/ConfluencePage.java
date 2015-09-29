package com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfluencePage {

    private String id;
    private Body body;
    private String title;
    private List<Ancestor> ancestors;
    private Space space;
    private String type;
    private Version version;
    private Metadata metadata;
    private Children children;

    public ConfluencePage() {
    }

    public ConfluencePage(String title, String spaceKey, String contents) {
        type = "page";
        this.title = title;
        space = new Space(spaceKey);
        body = new Body(contents);
    }

    public Children getChildren() {
        return children;
    }

    public void setChildren(Children children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Ancestor> getAncestors() {
        return ancestors;
    }

    public void setAncestors(List<Ancestor> ancestors) {
        this.ancestors = ancestors;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @JsonIgnore
    public int getVersionInt() {
        return version.getNumber();
    }

    @JsonIgnore
    public void setVersionInt(int ver) {
        version = new Version(ver);
    }

    @Override
    public String toString() {
        return "ConfluencePage{" +
                "id='" + id + '\'' +
                ", body=" + body +
                ", title='" + title + '\'' +
                ", ancestors=" + ancestors +
                ", space=" + space +
                ", type='" + type + '\'' +
                ", version=" + version +
                ", metadata=" + metadata +
                ", page=" + children +
                '}';
    }

    @JsonIgnore
    public String getSpaceKey() {
        return space.getKey();
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}