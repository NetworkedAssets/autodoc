package com.networkedassets.autodoc.documentation;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;
import com.google.common.base.Joiner;
import com.networkedassets.util.functional.Optionals;
import net.java.ao.Query;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/documentation/")
@Produces({MediaType.APPLICATION_JSON})
public class DocumentationService {
    private ActiveObjects ao;

    public DocumentationService(ActiveObjects ao) {
        this.ao = ao;
    }

    @Path("{project}/{repo}/{branch}/{doctype}")
    @GET
    public Response getDocumentationPiecesForProject(
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String doctype) throws UnsupportedEncodingException {
        String projectDec = URLDecoder.decode(project, "UTF-8");
        String repoDec = URLDecoder.decode(repo, "UTF-8");
        String branchDec = URLDecoder.decode(branch, "UTF-8");
        String doctypeDec = URLDecoder.decode(doctype, "UTF-8");

        if ("uml".equalsIgnoreCase(doctypeDec)) return getDocumentationPiece(projectDec, repoDec, branchDec, doctypeDec, "all");
        return ao.executeInTransaction(() ->
                getDocumentation(projectDec, repoDec, branchDec, doctypeDec)
                        .map(d -> Response.ok("{\"success\": true, \"documentationPieces\": [" + Joiner.on(",")
                                .join(Arrays.asList(d.getDocumentationPieces())
                                        .stream()
                                        .map(dp -> "{\"type\": \"" + dp.getPieceType() + "\","
                                                + "\"name\": \"" + dp.getPieceName() + "\"}")
                                        .collect(Collectors.toList()))
                                + "]}"))
                        .orElse(Response.status(404).entity(
                                "{\"success\": false, \"message\": \"Could not find requested documentation!\"}"
                        ))).build();
    }

    @Path("{project}/{repo}/{branch}/{doctype}/{docPieceName}{attribute:(/[^/]+?)?}")
    @GET
    public Response getDocumentationPiece(
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String docType,
            @PathParam("docPieceName") String docPieceName,
            @PathParam("attribute") String attribute) throws UnsupportedEncodingException {
        String projectDec = URLDecoder.decode(project, "UTF-8");
        String repoDec = URLDecoder.decode(repo, "UTF-8");
        String branchDec = URLDecoder.decode(branch, "UTF-8");
        String doctypeDec = URLDecoder.decode(docType, "UTF-8");
        String docPieceNameDec = URLDecoder.decode(docPieceName, "UTF-8");
        String attributeDec = URLDecoder.decode(attribute, "UTF-8");

        Optional<DocumentationPiece> documentationPiece = ao.executeInTransaction(() ->
                getDocumentation(projectDec, repoDec, branchDec, doctypeDec)
                        .flatMap(d -> getDocumentationPiece(d, docPieceNameDec))
        );
        final String finalAttributeDec = (!attributeDec.equals("")) ? attributeDec.substring(1, attributeDec.length()) : attributeDec; //attribute starts with reduntant /
        return buildDocumentationPieceResponse(documentationPiece, finalAttributeDec);
    }

    private Response getDocumentationPiece(String project, String repo, String branch, String docType, String docPieceName) throws UnsupportedEncodingException {
        return getDocumentationPiece(project, repo, branch, docType, docPieceName, "");
    }

    private Response buildDocumentationPieceResponse(Optional<DocumentationPiece> documentationPiece, String attribute) {
        Optional<String> jsonOptional = documentationPiece.map(d -> makeDocPieceJson(d, attribute));
        return jsonOptional
                .map(n -> Response.ok(n).build())
                .orElse(Response.status(404).entity("{\"success\": false, \"message\": \"Could not find requested documentation!\"}").build());
    }

    private String makeDocPieceJson(DocumentationPiece dp, String attribute) {
        if(attribute.isEmpty()){
            return dp.getContent();
        }else{
            JSONObject jsonObject = new JSONObject(dp.getContent());
            try {
                return String.format("{\"%s\": \"%s\"}", attribute, jsonObject.getString(attribute));
            } catch (JSONException e) {
                return null;
            }
        }
    }

    @Path("{project}/{repo}/{branch}/UML/{docPieceName}")
    @GET
    @Produces("application/json")
    public Response test(
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String docType,
            @PathParam("docPieceName") String docPieceName) throws IOException {
        String projectDec = URLDecoder.decode(project, "UTF-8");
        String repoDec = URLDecoder.decode(repo, "UTF-8");
        String branchDec = URLDecoder.decode(branch, "UTF-8");
        String doctypeDec = "UML";
        String docPieceNameDec = URLDecoder.decode(docPieceName, "UTF-8");

        Optional<DocumentationPiece> documentationPiece = ao.executeInTransaction(() ->
                getDocumentation(projectDec, repoDec, branchDec, doctypeDec)
                        .flatMap(d -> getDocumentationPiece(d, "all"))
        );
        final String JSON = documentationPiece.get().getContent(); //remove .get()?

        JsonDocumentationParser parser = new JsonDocumentationParser(JSON);
        Optional<String> composedJSON = parser.composeJSON(docPieceNameDec);

        return composedJSON.map(n -> Response.ok(n).build()).orElse(Response.status(404).build());
    }

    public Optional<Documentation> getDocumentation(String project, String repo, String branch, String documentationType) {
        Documentation[] documentations = ao.find(Documentation.class, Query.select()
                .where("PROJECT_KEY = ? AND REPO_SLUG = ? AND BRANCH_NAME = ? AND DOCUMENTATION_TYPE = ?",
                        project, repo, branch, documentationType));

        return Optionals.fromArrayOfOne(documentations);
    }

    public Optional<DocumentationPiece> getDocumentationPiece(Documentation doc, String docPieceName) {
        DocumentationPiece[] pieces = ao.find(DocumentationPiece.class, Query.select()
                .where("DOCUMENTATION_ID = ? AND PIECE_NAME = ?", doc.getID(), docPieceName));

        return Optionals.fromArrayOfOne(pieces);
    }

    @Path("{project}/{repo}/{branch}/{doctype}/{docPieceName}")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response postDocPiece(
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String docType,
            @PathParam("docPieceName") String docPieceName,
            @QueryParam("pieceType") String pieceType,
            String content) throws UnsupportedEncodingException {
        String projectDec = URLDecoder.decode(project, "UTF-8");
        String repoDec = URLDecoder.decode(repo, "UTF-8");
        String branchDec = URLDecoder.decode(branch, "UTF-8");
        String docTypeDec = URLDecoder.decode(docType, "UTF-8");
        String docPieceNameDec = URLDecoder.decode(docPieceName, "UTF-8");
        String pieceTypeDec = URLDecoder.decode(pieceType, "UTF-8");
        return ao.executeInTransaction(() -> {
            Documentation doc = findOrCreateDocumentation(projectDec, repoDec, branchDec, docTypeDec);
            DocumentationPiece piece = findOrCreateDocumentationPiece(doc, docPieceNameDec, pieceTypeDec);
            piece.setContent(content);
            piece.save();
            doc.save();

            return Response.ok("{\"success\": true}").build();
        });
    }

    public Documentation findOrCreateDocumentation(String project, String repo, String branch, String docType) {
        return getDocumentation(project, repo, branch, docType).orElseGet(() -> {
            Documentation doc = ao.create(Documentation.class);
            doc.setProjectKey(project);
            doc.setRepoSlug(repo);
            doc.setBranchName(branch);
            doc.setDocumentationType(docType);
            doc.save();

            return doc;
        });
    }

    public DocumentationPiece findOrCreateDocumentationPiece(Documentation doc, String docPieceName, String pieceType) {
        return getDocumentationPiece(doc, docPieceName).orElseGet(() -> {
            DocumentationPiece piece = ao.create(DocumentationPiece.class);
            piece.setDocumentation(doc);
            piece.setPieceName(docPieceName);
            piece.setPieceType(pieceType);
            piece.save();

            return piece;
        });
    }
}
