package com.networkedassets.autodoc.documentation;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.streams.thirdparty.api.ActivityService;
import com.google.common.base.Joiner;
import com.networkedassets.util.functional.Optionals;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/documentation/")
@Produces({MediaType.APPLICATION_JSON})
public class DocumentationService {
    private ActiveObjects ao;
    @SuppressWarnings("unused")
    private Logger log = LoggerFactory.getLogger(DocumentationService.class);
    private DocumentationActivityPoster documentationActivityPoster;
    private final String ERROR_JSON = "{\"success\": false, \"message\": \"Could not find requested documentation!\"}";

    public DocumentationService(ActiveObjects ao, ApplicationProperties applicationProperties
                                ,ActivityService activityService) {
        this.ao = ao;
        documentationActivityPoster = new DocumentationActivityPoster(applicationProperties, activityService);
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
                        .orElse(Response.status(404).entity(ERROR_JSON))).build();
    }

    @Path("{project}/{repo}/{branch}/{doctype}/{docPieceName}")
    @GET
    public Response getDocumentationPiece(
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String docType,
            @PathParam("docPieceName") String docPieceName) throws UnsupportedEncodingException {
        String projectDec = URLDecoder.decode(project, "UTF-8");
        String repoDec = URLDecoder.decode(repo, "UTF-8");
        String branchDec = URLDecoder.decode(branch, "UTF-8");
        String doctypeDec = URLDecoder.decode(docType, "UTF-8");
        String docPieceNameDec = URLDecoder.decode(docPieceName, "UTF-8");

        Optional<DocumentationPiece> documentationPiece = findDocumentationPiece(projectDec, repoDec, branchDec, doctypeDec, docPieceNameDec);

        return documentationPiece.map(this::makeDocPieceJson)
                .map(n -> Response.ok(n).build())
                .orElse(Response.status(404).entity(ERROR_JSON).build());
    }

    @Path("{project}/{repo}/{branch}/{doctype}/{docPieceName}/{attribute}")
    @GET
    public Response getDocumentationPieceByAttribute(
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

        Optional<DocumentationPiece> documentationPiece = findDocumentationPiece(projectDec, repoDec, branchDec, doctypeDec, docPieceNameDec);

        return documentationPiece.map(docPiece -> makeDocPieceJson(docPiece, attributeDec))
                .map(n -> Response.ok(n).build())
                .orElse(Response.status(404).entity(ERROR_JSON).build());
    }

    private String makeDocPieceJson(DocumentationPiece dp) {
        return dp.getContent();
    }

    private String makeDocPieceJson(DocumentationPiece dp, String attribute) {
        JSONObject jsonObject = new JSONObject(dp.getContent());
        try {
            return String.format("{\"%s\": \"%s\"}", attribute, jsonObject.getString(attribute));
        } catch (JSONException e) {
            return null;
        }
    }

    private Optional<DocumentationPiece> findDocumentationPiece(String projectDec, String repoDec, String branchDec, String doctypeDec, String docPieceNameDec) {
        return ao.executeInTransaction(() ->
                getDocumentation(projectDec, repoDec, branchDec, doctypeDec)
                        .flatMap(d -> getDocumentationPiece(d, docPieceNameDec)));
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

        Response response = ao.executeInTransaction(() -> {
            Documentation doc = findOrCreateDocumentation(projectDec, repoDec, branchDec, docTypeDec);
            DocumentationPiece piece = findOrCreateDocumentationPiece(doc, docPieceNameDec, pieceTypeDec);
            piece.setContent(content);
            piece.save();
            doc.save();

            return Response.ok("{\"success\": true}").build();
        });

        documentationActivityPoster.postDocumentationAdded(new DocumentationAdded(projectDec, repoDec, branchDec, docTypeDec, docPieceNameDec));

        return response;
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
