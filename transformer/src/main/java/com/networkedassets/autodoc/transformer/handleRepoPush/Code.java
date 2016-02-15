package com.networkedassets.autodoc.transformer.handleRepoPush;

import java.nio.file.Path;

public class Code {
    private Path codePath;

    public Code(Path codePath) {
        this.codePath = codePath;
    }

    public Path getCodePath() {
        return codePath;
    }

    public void setCodePath(Path codePath) {
        this.codePath = codePath;
    }
}
