package com.networkedassets.autodoc.documentation;

import net.java.ao.Entity;
import net.java.ao.schema.StringLength;

public interface DocumentationPiece extends Entity {
    String getPieceName();
    void setPieceName(String name);

    /**
     * Type of this documentation piece. Ex. for piece of javadoc this may be "class", "package"
     */
    String getPieceType();
    void setPieceType(String type);

    @StringLength(StringLength.UNLIMITED)
    String getContent();
    @StringLength(StringLength.UNLIMITED)
    void setContent(String content);

    Documentation getDocumentation();
    void setDocumentation(Documentation doc);
}
