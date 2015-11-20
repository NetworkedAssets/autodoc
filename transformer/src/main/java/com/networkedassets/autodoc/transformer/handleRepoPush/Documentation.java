package com.networkedassets.autodoc.transformer.handleRepoPush;

import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationType;

import java.util.List;

public class Documentation {
    private List<DocumentationPiece> pieces;
    private String project;
    private String repo;
    private String branch;
    private DocumentationType type;

    public Documentation(List<DocumentationPiece> pieces) {
        this.pieces = pieces;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public DocumentationType getType() {
        return type;
    }

    public void setType(DocumentationType type) {
        this.type = type;
    }

    public void setProjectInfo(String project, String repo, String branch) {
        setProject(project);
        setRepo(repo);
        setBranch(branch);
    }

    public List<DocumentationPiece> getPieces() {
        return pieces;
    }

    public void setPieces(List<DocumentationPiece> pieces) {
        this.pieces = pieces;
    }
}
