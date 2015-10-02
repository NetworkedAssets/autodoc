package com.networkedassets.autodoc.transformer.clients.git;

import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.networkedassets.autodoc.transformer.utils.HtmlFileConventer;

public class PlantUMLFileConverter implements HtmlFileConventer {

  @Override
  public String convert(String fileContent) {

    Document doc = Jsoup.parse(fileContent);
    String className = doc.select("h2.Title").first().text();

    StringBuffer sb = new StringBuffer();
    sb.append(className).append(" {\n");

    String prepfileContent = doc.select("div.summary~*").first().html();
    doc = Jsoup.parse(prepfileContent);
    Elements pres = doc.select("pre");
    sb.append(pres.stream().map(pre -> replaceMethodsAndFieldsVisibility(pre.text()))
        .collect(Collectors.joining("\n")));
    sb.append("}");
    return sb.toString();

  }

  public String getFileDescription(String fileContent) {

    Document doc = Jsoup.parse(fileContent);
    return doc.select("div.subTitle").first().text();

  }

  public String getFileName(String fileContent) {
    Document doc = Jsoup.parse(fileContent);
    return doc.select("h2.title").first().text();
  }

  //Todo
  private String replaceMethodsAndFieldsVisibility(String methodFieldName) {
    return methodFieldName;
  }

}
