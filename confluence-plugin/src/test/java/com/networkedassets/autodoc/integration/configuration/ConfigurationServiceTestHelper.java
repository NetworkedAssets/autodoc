package com.networkedassets.autodoc.integration.configuration;

import com.jayway.restassured.specification.RequestSpecification;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Source;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by mtulaza on 2016-02-29.
 */
public class ConfigurationServiceTestHelper {
    private static final RequestSpecification HTTPSvalidatedRequest = given()
            .relaxedHTTPSValidation();

    public static Branch getSampleBranch(int sourceId, String projectKey, String repoSlug, String branchId) {
        Source source = HTTPSvalidatedRequest
                .get("https://atlas.networkedassets.net:8050/sources/" + sourceId)
                .getBody().as(Source.class);
        return source
                .getProjectByKey(projectKey)
                .getRepoBySlug(repoSlug)
                .getBranchById(branchId);
    }
}
