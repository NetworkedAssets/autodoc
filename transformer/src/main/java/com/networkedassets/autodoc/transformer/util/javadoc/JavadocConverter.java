package com.networkedassets.autodoc.transformer.util.javadoc;

import com.eclipsesource.json.*;
import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Enum;
import com.github.markusbernhardt.xmldoclet.xjc.*;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.google.common.collect.ImmutableList;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.DocumentationPiece;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.DocumentationType;
import jersey.repackaged.com.google.common.collect.Lists;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory;
import org.json.JSONException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavadocConverter {

	public final static int PRETTY_PRINT_INDENT_FACTOR = 4;
	private final Path jsonJavaDocPath;

	public JavadocConverter(Path jsonJavaDocPath) {
		this.jsonJavaDocPath = jsonJavaDocPath;

	}

	public Documentation convert() throws JSONException, IOException {

		Documentation documentation = new Documentation(ImmutableList.of(
				new DocumentationPiece("MAIN_PIECE", "MAIN_PIECE",
						javadocToMainAsJson().toString(WriterConfig.PRETTY_PRINT)),
				new DocumentationPiece("INDEX", "INDEX", javadocToIndexAsJson().toString(WriterConfig.PRETTY_PRINT))));
		documentation.setType(DocumentationType.JAVADOC);
		return documentation;

	}

	protected JsonObject javadocToMainAsJson() throws IOException {

		JsonObject rootObj = Json.parse(new FileReader(this.jsonJavaDocPath.toString())).asObject();
		JsonValue packages = rootObj.get("root").asObject().get("package").asArray();

		JsonObject entities_ = Json.object();
		for (JsonValue item : packages.asArray()) {
			JsonObject package_ = Json.object().add("qualified", item.asObject().getString("name", "")).add("type",
					"package");
			item.asObject().names().stream().filter(names -> item.asObject().get(names).isArray())
					.forEach(entityType -> item.asObject().get(entityType).asArray().forEach(entity -> {

						entity.asObject().names().stream()
								.filter(entityTypes -> entity.asObject().get(entityTypes).isArray())
								.forEach(classes -> entity.asObject().get(classes).asArray().forEach(clazz -> {
							clazz.asObject().add("modifiers", addModifiers(clazz.asObject()));
							removeModifiers(clazz.asObject());
						}));
						entity.asObject().add("modifiers", addModifiers(entity.asObject())).add("type",
								entityType);
						package_.add(entity.asObject().getString("qualified", ""),
								removeModifiers(entity.asObject()));
					}));
			entities_.add(package_.getString("qualified", ""), package_);
		}
		rootObj.remove("root");
		rootObj.add("entities", entities_);
		return rootObj;
	}

	private JsonArray addModifiers(JsonObject obj) {

		JsonArray arr = Json.array().asArray();
		obj.names().stream().filter(modifiers -> obj.get(modifiers).isTrue() && !modifiers.equals("included")
				&& !modifiers.equals("varArgs")).forEach(arr::add);
		return arr;
	}

	private JsonObject removeModifiers(JsonObject obj) {

		List<String> target = Lists.newArrayList();
		obj.names().stream().filter(modifiers -> obj.get(modifiers).isFalse() && !modifiers.equals("included")
				&& !modifiers.equals("varArgs")).forEach(target::add);
		target.forEach(obj::remove);
		return obj;
	}

	private JsonObject javadocToIndexAsJson() throws JSONException, IOException {

		JsonObject rootObj = Json.parse(new FileReader(this.jsonJavaDocPath.toString())).asObject();
		JsonValue packages = rootObj.get("root").asObject().get("package").asArray();

		JsonObject entities = Json.object();
		JsonArray arrayOfPackages = Json.array().asArray();
		for (JsonValue item : packages.asArray()) {
			JsonObject package_ = Json.object();
			package_.add("name", item.asObject().getString("name", ""));
			arrayOfPackages.add(package_);
			JsonArray arrayOfClasses = Json.array().asArray();

			item.asObject().names().stream().filter(name -> item.asObject().get(name).isArray())
					.forEach(s -> item.asObject().get(s).asArray().forEach(e -> {
						JsonObject classes = Json.object().add("name", e.asObject().getString("name", ""))
								.add("qualified", e.asObject().getString("qualified", "")).add("type", s);
						arrayOfClasses.add(classes);

					}));
			package_.add("children", arrayOfClasses);
		}

		entities.add("package", arrayOfPackages);
		rootObj.remove("root");
		rootObj.add("entities", entities);

		return rootObj;
	}

    public static String docToString(java.lang.Class<?> clazz, Object val) throws JAXBException {
        Map<String, Object> properties = new HashMap<>(2);
        properties.put(MarshallerProperties.MEDIA_TYPE, "application/json");
        properties.put(MarshallerProperties.JSON_INCLUDE_ROOT, true);
        properties.put(MarshallerProperties.INDENT_STRING, true);
        JAXBContext contextObj = JAXBContextFactory.createContext(new java.lang.Class[] { clazz, ObjectFactory.class },
                properties);
        Marshaller marshaller = contextObj.createMarshaller();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        marshaller.marshal(val, baos);
        return baos.toString();
    }

	public static Documentation convert(Root docRoot) throws JAXBException {

		JAXBContext jc = JAXBContext.newInstance(Root.class);
		Marshaller marshaller  = jc.createMarshaller();
		marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(docRoot, System.out);

        Documentation doc = new Documentation(new ArrayList<>());
        doc.setType(DocumentationType.JAVADOC);

        List<DocumentationPiece> pieces = doc.getPieces();
        String index = docToString(Index.class, docRoot.getIndex());
        pieces.add(new DocumentationPiece("index", "index", index));

        for (Package p : docRoot.getPackage()) {
            for (Enum e : p.getEnum()) {
                String en = docToString(Enum.class, e);
                pieces.add(new DocumentationPiece(e.getQualified(), "enum", en));
            }
            for (Interface i : p.getInterface()) {
                String en = docToString(Interface.class, i);
                pieces.add(new DocumentationPiece(i.getQualified(), "interface", en));
            }
            for (Class e : p.getClazz()) {
                String en = docToString(Class.class, e);
                pieces.add(new DocumentationPiece(e.getQualified(), "class", en));
            }
        }

        return doc;
	}
}
