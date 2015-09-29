package com.networkedassets.autodoc.transformer.clients.atlassian.api;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClient;
import com.networkedassets.autodoc.transformer.clients.atlassian.HttpClientConfig;
import com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData.Ancestor;
import com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData.ConfluencePage;
import com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData.ContentSearchPage;
import com.networkedassets.util.functional.Optionals;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfluenceClient extends HttpClient {

    public ConfluenceClient(HttpClientConfig config) {
        super(config);
    }

    /**
     * Creates a new page
     *
     * @param page description of a page to be created
     * @return a {@link ConfluencePage} object filled with details
     * @throws UnirestException
     */
    @Nonnull
    public ConfluencePage createPage(ConfluencePage page) throws UnirestException {
        Preconditions.checkNotNull(page);

        ConfluencePage res = Unirest.post(getBaseUrl() + "/rest/api/content")
                .header("Content-Type", "application/json")
                .basicAuth(getUsername(), getPassword())
                .body(page).asObject(ConfluencePage.class).getBody();
        Verify.verifyNotNull(res.getId(), "Could not create the page");
        putLabel(res.getId(), "networkedassets-javadoc"); // we tag all of our pages with this
        return res;
    }

    /**
     * Creates a new page
     *
     * @param title    the title of the page to be posted
     * @param spaceKey the key of the space the page is going to be posted in
     * @param contents contents of the new page
     * @param parentId id of the parent page of the new page; may be null
     * @return a page object filled with page details
     * @throws UnirestException
     */
    @Nonnull
    public ConfluencePage createPage(@Nonnull String title, @Nonnull String spaceKey, @Nonnull String contents,
                                     @Nullable String parentId)
            throws UnirestException {
        Preconditions.checkNotNull(title);
        Preconditions.checkNotNull(spaceKey);
        Preconditions.checkNotNull(contents);

        ConfluencePage page = new ConfluencePage(title, spaceKey, contents);
        if (parentId != null) {
            page.setAncestors(ImmutableList.of(new Ancestor(parentId)));
        }

        return createPage(page);
    }

    /**
     * Creates javadoc page for a given project/repo/branch in a given space
     *
     * @param spaceKey            space to create the page in
     * @param projectKey          project's key
     * @param repoSlug            repo's slug
     * @param branchId            branch's id
     * @param fullClassName       full name of the javadoced class (including package)
     * @param contents            javadoc content in Confluence Storage Format
     * @param javadocRootParentId id of the parent of the javadoc root page, may be null
     * @return a representation of the created class
     * @throws UnirestException
     */
    @Nonnull
    public ConfluencePage createJavadocPage(@Nonnull String spaceKey, @Nonnull String projectKey,
                                            @Nonnull String repoSlug, @Nonnull String branchId,
                                            @Nonnull String fullClassName, @Nonnull String contents,
                                            @Nullable String javadocRootParentId)
            throws UnirestException {
        Preconditions.checkNotNull(spaceKey);
        Preconditions.checkNotNull(projectKey);
        Preconditions.checkNotNull(repoSlug);
        Preconditions.checkNotNull(branchId);
        Preconditions.checkNotNull(fullClassName);
        Preconditions.checkNotNull(contents);

        String pageTitle = getJavadocPageName(projectKey, repoSlug, branchId, fullClassName);
        String rootId = getJavadocRootId(spaceKey, projectKey, repoSlug, branchId, javadocRootParentId);

        return createPage(pageTitle, spaceKey, contents, rootId);
    }

    /**
     * Puts a label on a page with given id
     *
     * @param pageId    id of the page that is supposed to be given a label
     * @param labelName label name
     * @return weather the label was put successfully
     * @throws UnirestException
     */
    public boolean putLabel(@Nonnull String pageId, @Nonnull String labelName) throws UnirestException {
        Preconditions.checkNotNull(pageId);
        Preconditions.checkNotNull(labelName);

        return Unirest.post(String.format("%s/rest/api/content/%s/label", getBaseUrl(), pageId))
                .header("Content-Type", "application/json")
                .body(String.format("[{\"name\": \"%s\"}]", labelName))
                .asJson().getStatus() == 200;
    }

    /**
     * Gets a name for a javadoc page for a given project/repo/branch
     *
     * @param projectKey    project's key
     * @param repoSlug      repo's slug
     * @param branchId      branch's id
     * @param fullClassName full name of the javadoced class (including package)
     * @return the name of javadoc page
     */
    @Nonnull
    public String getJavadocPageName(@Nonnull String projectKey, @Nonnull String repoSlug, @Nonnull String branchId,
                                     @Nonnull String fullClassName) {
        return String.format("%s [%s/%s/%s]", fullClassName, projectKey, repoSlug, branchId);
    }

    /**
     * Gets the id a javadoc root page for given project/repo/branch in the given space. Creates the page if necessary.
     *
     * @param spaceKey            space's key
     * @param projectKey          project's key
     * @param repoSlug            repo's slug
     * @param branchId            branch's id
     * @param javadocRootParentId id of the parent of the javadoc root page, may be null;
     *                            there is no checking that the existing page is a child of this page
     * @return id of the javadoc root page
     */
    @Nonnull
    public String getJavadocRootId(@Nonnull String spaceKey, @Nonnull String projectKey, @Nonnull String repoSlug,
                                   @Nonnull String branchId, @Nullable String javadocRootParentId)
            throws UnirestException {
        Preconditions.checkNotNull(spaceKey);
        Preconditions.checkNotNull(projectKey);
        Preconditions.checkNotNull(repoSlug);
        Preconditions.checkNotNull(branchId);

        String javadocTitle = getJavadocPageName(projectKey, repoSlug, branchId, "javadoc");
        // === javadoc [NAATLAS/autodoc/master]

        Optional<ConfluencePage> javadocRoot = findPage(spaceKey, javadocTitle);

        return Optionals.orElseGetThrowing(javadocRoot,
                () -> createPage(javadocTitle, spaceKey, "JAVADOC ROOT", javadocRootParentId)).getId();
    }

    /**
     * Finds a Confluence page with given title, located in given space
     *
     * @param spaceKey  space to search in
     * @param pageTitle page title to search for
     * @return a representation of a found confluence page on {@link Optional#empty()}
     * @throws UnirestException
     */
    @Nonnull
    public Optional<ConfluencePage> findPage(String spaceKey, String pageTitle) throws UnirestException {
        return Unirest.get(getBaseUrl() + "/rest/api/content/search")
                .queryString("cql", String.format("space='%s' AND title='%s'", spaceKey, pageTitle))
                .asObject(ContentSearchPage.class).getBody().getResults().stream().findFirst();
    }

    /**
     * Removes all javadoc pages related to a given project/repo/branch in a given space
     *
     * @param spaceKey   key of the space containing the pages to be removed
     * @param projectKey key of the stash project related to the pages to be removed
     * @param repoSlug   slug of the repo related to the pages to be removed
     * @param branchId   id of the branch related to the pages to be removed
     * @throws UnirestException
     */
    public void removeJavadocPages(@Nonnull String spaceKey, @Nonnull String projectKey, @Nonnull String repoSlug,
                                   @Nonnull String branchId) throws UnirestException {
        Preconditions.checkNotNull(spaceKey);
        Preconditions.checkNotNull(projectKey);
        Preconditions.checkNotNull(repoSlug);
        Preconditions.checkNotNull(branchId);

        String url = String.format("%s/rest/api/content/search", getBaseUrl());

        String cqlTemplate = "space='%s' AND label='%s-%s-%s'";
        String cql = String.format(cqlTemplate, spaceKey, projectKey, repoSlug, branchId);

        HttpResponse<ContentSearchPage> response = Unirest.get(url)
                .queryString("cql", cql)
                .queryString("limit", 9999)
                .queryString("expand", "space,version")
                .basicAuth(getUsername(), getPassword())
                .asObject(ContentSearchPage.class);

        List<ConfluencePage> pages = getAll(response.getBody());

        Verify.verify(pages.stream().allMatch(p -> p.getSpaceKey().equals(spaceKey)), "Pages with unexpected space!");

        for (ConfluencePage p : pages) {
            removePage(p);
        }
    }

    /**
     * Removes page with given id
     *
     * @param id id of the page to be removed
     * @return weather the page has been removed
     * @throws UnirestException
     */
    public boolean removePage(@Nonnull String id) throws UnirestException {
        Preconditions.checkNotNull(id);

        return Unirest.delete(String.format("%s/rest/api/content/%s", getBaseUrl(), id))
                .basicAuth(getUsername(), getPassword()).asJson().getStatus() == 204;
    }

    /**
     * Removes given page. The page is only required to have an id.
     *
     * @param page page to remove
     * @return weather the page has been removed
     * @throws UnirestException
     */
    public boolean removePage(@Nonnull ConfluencePage page) throws UnirestException {
        Preconditions.checkNotNull(page);

        return removePage(page.getId());
    }

    private List<ConfluencePage> getAll(ContentSearchPage body) throws UnirestException {
        List<ConfluencePage> pages = new ArrayList<>(body.getResults());

        String nextUrl;
        while (body.getLinks() != null && (nextUrl = body.getLinks().getNext()) != null) {
            body = Unirest.get(getBaseUrl() + nextUrl)
                    .basicAuth(getUsername(), getPassword())
                    .asObject(ContentSearchPage.class).getBody();
            pages.addAll(body.getResults());
        }

        return pages;
    }

}
