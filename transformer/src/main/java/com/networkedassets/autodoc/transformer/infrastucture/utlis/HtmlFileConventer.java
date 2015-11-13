package com.networkedassets.autodoc.transformer.infrastucture.utlis;

public interface HtmlFileConventer {

	public String convert(String fileContent);

	public String getFileName(String fileContent);

	public String getFileDescription(String fileContent);

}
