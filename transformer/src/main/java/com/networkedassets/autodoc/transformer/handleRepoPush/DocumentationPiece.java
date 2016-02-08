package com.networkedassets.autodoc.transformer.handleRepoPush;

public class DocumentationPiece {
	private String pieceName;
	private String pieceType;
	private String content;

	public DocumentationPiece(String pieceName, String pieceType, String content) {
		this.pieceName = pieceName;
		this.pieceType = pieceType;
		this.content = content;
	}

	public String getPieceName() {
		return pieceName;
	}

	public void setPieceName(String pieceName) {
		this.pieceName = pieceName;
	}

	public String getPieceType() {
		return pieceType;
	}

	public void setPieceType(String pieceType) {
		this.pieceType = pieceType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
