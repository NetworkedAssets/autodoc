package com.networkedassets.autodoc.transformer.utils.data;

import java.util.Map;

import com.google.common.base.MoreObjects;

public class HtmlFile {

	private String fileName;
	private String filePath;
	private String fileContent;
	private Map<String, Object> additionalProperties;

	public HtmlFile(String fileName, String filePath, String fileContent) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileContent = fileContent;
	}

	public HtmlFile(String fileName, String filePath) {
		this.fileName = fileName;
		this.filePath = filePath;
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

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this.getClass()).add("fileName", fileName).add("filePath", filePath)
				.add("fileContent", fileContent).toString();
	}

}
