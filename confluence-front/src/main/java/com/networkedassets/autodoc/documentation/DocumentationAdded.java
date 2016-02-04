package com.networkedassets.autodoc.documentation;

public class DocumentationAdded {
    private final String project;
    private final String repo;
    private final String branch;
    private final String docType;
    private final String docPieceNameDec;

    public DocumentationAdded(String projectDec, String repoDec, String branchDec, String docTypeDec, String docPieceNameDec) {
        project = projectDec;
        repo = repoDec;
        branch = branchDec;
        docType = docTypeDec;
        this.docPieceNameDec = docPieceNameDec;
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

    public String getDocPieceNameDec() {
        return docPieceNameDec;
    }
}
