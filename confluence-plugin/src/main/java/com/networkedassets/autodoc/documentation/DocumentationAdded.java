package com.networkedassets.autodoc.documentation;

import java.util.Objects;

public class DocumentationAdded {
    private final String project;
    private final String repo;
    private final String branch;
    private final String docType;
    private final String docPieceName;
    private String username;

    public DocumentationAdded(String projectDec, String repoDec, String branchDec, String docTypeDec, String docPieceName, String username) {
        project = projectDec;
        repo = repoDec;
        branch = branchDec;
        docType = docTypeDec;
        this.docPieceName = docPieceName;
        this.username = username;
    }

    public String getProject() {
        return project;
    }

    public String getRepo() {
        return repo;
    }

    public String getBranch() {
        return branch;
    }

    public String getDocType() {
        return docType;
    }

    public String getDocPieceName() {
        return docPieceName;
    }

    // OBJECTS WITH DIFFERENT docPieceName ARE CONSIDERED EQUAL. This is intentional.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentationAdded that = (DocumentationAdded) o;
        return Objects.equals(project, that.project) &&
                Objects.equals(repo, that.repo) &&
                Objects.equals(branch, that.branch) &&
                Objects.equals(docType, that.docType);
    }

    // OBJECTS WITH DIFFERENT docPieceName ARE CONSIDERED EQUAL. This is intentional.
    @Override
    public int hashCode() {
        return Objects.hash(project, repo, branch, docType);
    }

    public String getUsername() {
        return username;
    }
}
