package com.networkedassets.autodoc.documentation;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.networkedassets.util.functional.Optionals;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DocumentationDAO {
    private Logger log = LoggerFactory.getLogger(DocumentationDAO.class);
    private ActiveObjects ao;

    public DocumentationDAO(ActiveObjects ao) {
        this.ao = ao;
    }

    public Optional<Documentation> findDocumentation(String project, String repo, String branch, String documentationType) {
        Documentation[] documentations = ao.find(Documentation.class, Query.select()
                .where("PROJECT_KEY = ? AND REPO_SLUG = ? AND BRANCH_NAME = ? AND DOCUMENTATION_TYPE = ?",
                        project, repo, branch, documentationType));

        return Optionals.fromArrayOfOne(documentations);
    }

    public Optional<DocumentationPiece> findDocumentationPiece(String project, String repo, String branch, String docType, String docPieceName) {
        return ao.executeInTransaction(() ->
                findDocumentation(project, repo, branch, docType)
                        .flatMap(doc -> findDocumentationPieceInDocumentation(doc, docPieceName)));
    }

    public Optional<DocumentationPiece> findDocumentationPieceInDocumentation(Documentation doc, String docPieceName) {
        DocumentationPiece[] pieces = ao.find(DocumentationPiece.class, Query.select()
                .where("DOCUMENTATION_ID = ? AND PIECE_NAME = ?", doc.getID(), docPieceName));

        return Optionals.fromArrayOfOne(pieces);
    }

    public List<DocumentationPiece> findDocumentationPieceWithQuery(String project, String repo, String branch, String doctype, String query) {
        return ao.executeInTransaction(() ->
                findDocumentation(project, repo, branch, doctype).map(doc -> {
                    final String generalizedQuery = "%" + query + "%";
                    final DocumentationPiece[] documentationPieces = ao.find(DocumentationPiece.class,
                            Query.select().where("PIECE_TYPE <> 'index' AND PIECE_TYPE <> 'INDEX' AND " +
                                    "DOCUMENTATION_ID = ? AND CONTENT LIKE ?", doc.getID(), generalizedQuery));
                    return Arrays.asList(documentationPieces);
                }).orElse(Collections.emptyList()));
    }

    public Documentation findOrCreateDocumentation(String project, String repo, String branch, String docType) {
        return findDocumentation(project, repo, branch, docType).orElseGet(() -> {
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
        return findDocumentationPieceInDocumentation(doc, docPieceName).orElseGet(() -> {
            DocumentationPiece piece = ao.create(DocumentationPiece.class);
            piece.setDocumentation(doc);
            piece.setPieceName(docPieceName);
            piece.setPieceType(pieceType);
            piece.save();

            return piece;
        });
    }

    public void deleteDocumentationPiecesWithOtherVersionId(String project, String repo, String branch, String docType, String versionId) {
        Optional<Documentation> documentation = findDocumentation(project, repo, branch, docType);
        documentation.ifPresent(doc -> {
            List<DocumentationPiece> diffVersionIdDocPieceList = Arrays.asList(
                    ao.find(DocumentationPiece.class, Query.select().where("DOCUMENTATION_ID = ? AND VERSION_ID <> ?", doc.getID(), versionId)
                    ));
            diffVersionIdDocPieceList.forEach(docPieceToDelete -> {
                ao.delete(docPieceToDelete);
                log.info("DocumentationPiece named: {} with versionId {} deleted", docPieceToDelete.getPieceName(), docPieceToDelete.getVersionId());
            });
        });
    }
}
