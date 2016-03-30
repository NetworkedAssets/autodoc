package com.networkedassets.autodoc.documentation;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.streams.thirdparty.api.ActivityService;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.networkedassets.autodoc.util.Debouncer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Path("/documentation/")
@Produces({MediaType.APPLICATION_JSON})
public class DocumentationService {
    private final String ERROR_JSON = "{\"success\": false, \"message\": \"Could not find requested documentation!\"}";
    private DocumentationRepository repository;
    @SuppressWarnings("unused")
    private Logger log = LoggerFactory.getLogger(DocumentationService.class);
    private Consumer<DocumentationAdded> documentationActivityPoster;

    public DocumentationService(DocumentationRepository repository, ApplicationProperties applicationProperties, ActivityService activityService) {
        this.repository = repository;
        documentationActivityPoster = new Debouncer<>(
                new DocumentationActivityPoster(applicationProperties, activityService),
                5000); // 5s
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

        if ("uml".equalsIgnoreCase(doctypeDec))
            return getDocumentationPiece(projectDec, repoDec, branchDec, doctypeDec, "all");
        return repository.findDocumentation(projectDec, repoDec, branchDec, doctypeDec)
                .map(d -> Response.ok("{\"success\": true, \"documentationPieces\": [" + Joiner.on(",")
                        .join(Arrays.asList(d.getDocumentationPieces())
                                .stream()
                                .map(dp -> "{\"type\": \"" + dp.getPieceType() + "\","
                                        + "\"name\": \"" + dp.getPieceName() + "\"}")
                                .collect(Collectors.toList()))
                        + "]}"))
                .orElse(Response.status(404).entity(ERROR_JSON)).build();
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

        Optional<DocumentationPiece> documentationPiece = repository.findDocumentationPiece(projectDec, repoDec, branchDec, doctypeDec, docPieceNameDec);

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

        Optional<DocumentationPiece> documentationPiece = repository.findDocumentationPiece(projectDec, repoDec, branchDec, doctypeDec, docPieceNameDec);

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

    @Path("{project}/{repo}/{branch}/{doctype}/search")
    @GET
    public Response searchDocumentation(
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String doctype,
            @QueryParam("q") String query) throws UnsupportedEncodingException {

        if (Strings.isNullOrEmpty(query)) return Response.ok("{\"results\": []}").build();

        String projectDec = URLDecoder.decode(project, "UTF-8");
        String repoDec = URLDecoder.decode(repo, "UTF-8");
        String branchDec = URLDecoder.decode(branch, "UTF-8");
        String queryDec = URLDecoder.decode(query, "UTF-8");
        String doctypeDec = URLDecoder.decode(doctype, "UTF-8");

        List<DocumentationPiece> searchResult = repository.findDocumentationPieceWithQuery(projectDec, repoDec, branchDec, doctypeDec, queryDec);

        final List<String> results = searchResult.stream()
                .map(dp -> "\"" + dp.getPieceName() + "\"")
                .collect(Collectors.toList());

        return Response.ok(String.format("{\"results\": [%s]}", Joiner.on(",").join(results))).build();
    }

    @Path("{project}/{repo}/{branch}/{doctype}/{docPieceName}")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createDocumentationPiece(
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String docType,
            @PathParam("docPieceName") String docPieceName,
            @QueryParam("pieceType") String pieceType,
            @QueryParam("versionId") String versionId,
            String content) throws UnsupportedEncodingException {
        String projectDec = URLDecoder.decode(project, "UTF-8");
        String repoDec = URLDecoder.decode(repo, "UTF-8");
        String branchDec = URLDecoder.decode(branch, "UTF-8");
        String docTypeDec = URLDecoder.decode(docType, "UTF-8");
        String docPieceNameDec = URLDecoder.decode(docPieceName, "UTF-8");
        String pieceTypeDec = URLDecoder.decode(pieceType, "UTF-8");
        String versionIdDec = URLDecoder.decode(versionId, "UTF-8");

        boolean result = repository.editOrCreateDocumentationPiece(content, projectDec, repoDec, branchDec, docTypeDec, docPieceNameDec, pieceTypeDec, versionIdDec);

        documentationActivityPoster.accept(new DocumentationAdded(projectDec, repoDec, branchDec, docTypeDec,
                docPieceNameDec, AuthenticatedUserThreadLocal.getUsername()));

        return Response.ok("{\"success\": " + result + "}").build();
    }

    @Path("{project}/{repo}/{branch}/{doctype}")
    @DELETE
    public Response deleteRedundantDocumentationPieces(
            @PathParam("project") String project,
            @PathParam("repo") String repo,
            @PathParam("branch") String branch,
            @PathParam("doctype") String docType,
            @QueryParam("versionId") final String versionId) throws UnsupportedEncodingException {
        if(Strings.isNullOrEmpty(versionId)) {
            Response.status(400).entity("{\"success\":\"false\", \"reason\":\"versionId QueryParam is required\"}");
        }
        String projectDec = URLDecoder.decode(project, "UTF-8");
        String repoDec = URLDecoder.decode(repo, "UTF-8");
        String branchDec = URLDecoder.decode(branch, "UTF-8");
        String docTypeDec = URLDecoder.decode(docType, "UTF-8");
        String versionIdDec = URLDecoder.decode(versionId, "UTF-8");

        repository.deleteDocumentationPiecesWithOtherVersionId(projectDec, repoDec, branchDec, docTypeDec, versionIdDec);
        //maybe: return some other response if there was an error when deleting
        return Response.ok("{\"success\":\"true\"}").build();
    }
}
