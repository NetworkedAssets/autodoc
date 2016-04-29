package com.networkedassets.autodoc.integration;

public class TransformerConstants {
	private static final String HOST = System.getProperty("transHost");
	private static final int PORT = Integer.parseInt(System.getProperty("transPort"));
	private static final String PATH = System.getProperty("transPath");
	
	public static String getHost() {
		return HOST;
	}

	public static String getPath() {
		return (PATH != null) ? PATH : "";
	}

	public static int getPort() {
		return PORT;
	}
}
