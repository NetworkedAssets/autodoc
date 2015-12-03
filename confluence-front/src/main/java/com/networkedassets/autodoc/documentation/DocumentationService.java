package com.networkedassets.autodoc.documentation;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.google.common.base.Joiner;
import com.networkedassets.util.functional.Optionals;
import net.java.ao.Query;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
    public String getDocumentationPiecesForProject(
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String doctype) {
        if ("uml".equalsIgnoreCase(doctype)) return getDocumentationPiece(project, repo, branch, doctype, "all");
        return ao.executeInTransaction(() ->
                getDocumentation(project, repo, branch, doctype)
                        .map(d -> "{\"success\": true, \"documentationPieces\": [" + Joiner.on(",")
                                .join(Arrays.asList(d.getDocumentationPieces())
                                        .stream()
                                        .map(dp -> "{\"type\": \"" + dp.getPieceType() + "\","
                                                + "\"name\": \"" + dp.getPieceName() + "\"}")
                                        .collect(Collectors.toList()))
                                + "]}")
                        .orElse("{\"success\": false, \"message\": \"Could not find requested documentation!\"}"));
    }

    @Path("{project}/{repo}/{branch}/{doctype}/{docPieceName}")
    @GET
    public String getDocumentationPiece(
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String docType,
            @PathParam("docPieceName") String docPieceName) {
        return ao.executeInTransaction(() ->
                getDocumentation(project, repo, branch, docType)
                        .flatMap(d -> getDocumentationPiece(d, docPieceName))
                        .map(this::makeDocPieceJson)
                        .orElse("{\"success\": false, \"message\": \"Could not find requested documentation!\"}"));
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

    private String makeDocPieceJson(DocumentationPiece dp) {
        return dp.getContent();
    }

    @Path("{project}/{repo}/{branch}/{doctype}/{docPieceName}")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public String postDocPiece(
            @Context
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String docType,
            @PathParam("docPieceName") String docPieceName,
            @QueryParam("pieceType") String pieceType,
            String content) {
        return ao.executeInTransaction(() -> {
            Documentation doc = findOrCreateDocumentation(project, repo, branch, docType);
            DocumentationPiece piece = findOrCreateDocumentationPiece(doc, docPieceName, pieceType);
            piece.setContent(content);
            piece.save();
            doc.save();

            return "{\"success\": true}";
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