package com.networkedassets.autodoc.documentation;

import net.java.ao.Entity;
import net.java.ao.OneToMany;

public interface Documentation extends Entity {
    String getDocumentationType();
    void setDocumentationType(String type);

    String getProjectKey();
    void setProjectKey(String key);

    String getRepoSlug();
    void setRepoSlug(String repoSlug);

    String getBranchName();
    void setBranchName(String branchName);

    @OneToMany(reverse = "getDocumentation")
    DocumentationPiece[] getDocumentationPieces();
}
