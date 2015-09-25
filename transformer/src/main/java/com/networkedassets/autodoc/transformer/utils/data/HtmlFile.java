package com.networkedassets.autodoc.transformer.utils.data;

import com.google.common.base.MoreObjects;

public class HtmlFile {

  private String fileName;
  private String filePath;
  private String fileContent;

  public HtmlFile(String fileName, String filePath, String fileContent) {
    this.fileName = fileName;
    this.filePath = filePath;
    this.fileContent = fileContent;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getFileContent() {
    return fileContent;
  }

  public void setFileContent(String fileContent) {
    this.fileContent = fileContent;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this.getClass()).add("fileName", fileName)
        .add("filePath", filePath).add("fileContent", fileContent).toString();
  }

}
