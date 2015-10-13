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
import com.networkedassets.autodoc.transformer.clients.atlassian.confluenceData.PageVersion;
import com.networkedassets.autodoc.transformer.utils.Consts;
import com.networkedassets.util.functional.Optionals;
import com.networkedassets.util.functional.Throwing;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfluenceClient extends HttpClient {

	private static final Logger log = LoggerFactory.getLogger(ConfluenceClient.class);
	private String javadocRootId;

	public ConfluenceClient(HttpClientConfig config) {
		super(config);
	}

	/**
	 * Creates a new page
	 *
	 * @param page
	 *            description of a page to be created
	 * @return a {@link ConfluencePage} object filled with details
	 * @throws UnirestException
	 */
	@Nonnull
	public ConfluencePage createPage(ConfluencePage page) throws UnirestException {
		Preconditions.checkNotNull(page);

		log.debug("Creating page with title: {}", page.getTitle());

		HttpResponse<ConfluencePage> response = Unirest.post(getBaseUrl() + "/rest/api/content")
				.header("Content-Type", "application/json").basicAuth(getUsername(), getPassword()).body(page)
				.asObject(ConfluencePage.class);
		ConfluencePage res = response.getBody();

		if (res.getId() == null) {
			try {
				RuntimeException e = new RuntimeException(
						"Got invalid response: " + IOUtils.toString(response.getRawBody()));
				log.error("", e);
				throw new RuntimeException(e);
			} catch (IOException e) {
				log.error("Herd u lik errorz");
				throw new RuntimeException("Could not convert body to string");
			}
		}

		log.debug("Created page with id: {}", res.getId());

		putLabel(res.getId(), "networkedassets-javadoc"); // we tag all of our
															// pages with this
		return res;
	}

	/**
	 * Creates a new page
	 *
	 * @param title
	 *            the title of the page to be posted
	 * @param spaceKey
	 *            the key of the space the page is going to be posted in
	 * @param contents
	 *            contents of the new page
	 * @param parentId
	 *            id of the parent page of the new page; may be null
	 * @return a page object filled with page details
	 * @throws UnirestException
	 */
	@Nonnull
	public ConfluencePage createPage(@Nonnull String title, @Nonnull String spaceKey, @Nonnull String contents,
			@Nullable String parentId) throws UnirestException {
		Preconditions.checkNotNull(title);
		Preconditions.checkNotNull(spaceKey);
		Preconditions.checkNotNull(contents);

		ConfluencePage page = new ConfluencePage(title, spaceKey, contents);
		if (parentId != null && !Throwing.didThrow(() -> Verify.verify(Long.parseLong(parentId) > 0))) {
			page.setAncestors(ImmutableList.of(new Ancestor(parentId)));
		}

		return createPage(page);
	}

	/**
	 * Creates javadoc page for a given project/repo/branch in a given space
	 *
	 * @param spaceKey
	 *            space to create the page in
	 * @param projectKey
	 *            project's key
	 * @param repoSlug
	 *            repo's slug
	 * @param branchId
	 *            branch's id
	 * @param fullClassName
	 *            full name of the javadoced class (including package)
	 * @param contents
	 *            javadoc content in Confluence Storage Format
	 * @param javadocRootParentId
	 *            id of the parent of the javadoc root page, may be null
	 * @return a representation of the created class
	 * @throws UnirestException
	 */
	@Nonnull
	public ConfluencePage createJavadocPage(@Nonnull String spaceKey, @Nonnull String projectKey,
			@Nonnull String repoSlug, @Nonnull String branchId, @Nonnull String fullClassName, @Nonnull String contents,
			@Nullable String javadocRootParentId) throws UnirestException {
		Preconditions.checkNotNull(spaceKey);
		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repoSlug);
		Preconditions.checkNotNull(branchId);
		Preconditions.checkNotNull(fullClassName);
		Preconditions.checkNotNull(contents);

		String pageTitle = getJavadocPageName(projectKey, repoSlug, branchId, fullClassName);
		String rootId = getJavadocRootId(spaceKey, projectKey, repoSlug, branchId, javadocRootParentId);

		ConfluencePage page = createPage(pageTitle, spaceKey, contents, rootId);
		putLabel(page.getId(), String.format(Consts.LABEL_TEMPLATE, projectKey, repoSlug, branchId));
		return page;
	}

	/**
	 * Creates javadoc page for a given project/repo/branch in a given space
	 *
	 * @param spaceKey
	 *            space to create the page in
	 * @param projectKey
	 *            project's key
	 * @param repoSlug
	 *            repo's slug
	 * @param branchId
	 *            branch's id
	 * @param fullClassName
	 *            full name of the javadoced class (including package)
	 * @param contents
	 *            javadoc content in Confluence Storage Format
	 * @param javadocRootParentId
	 *            id of the parent of the javadoc root page, may be null
	 * @return a representation of the created class
	 * @throws UnirestException
	 */
	@Nonnull
	public ConfluencePage createUmlPage(@Nonnull String spaceKey, @Nonnull String projectKey, @Nonnull String repoSlug,
			@Nonnull String branchId, @Nonnull String fullClassName, @Nonnull String contents,
			@Nullable String javadocRootParentId) throws UnirestException {
		Preconditions.checkNotNull(spaceKey);
		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repoSlug);
		Preconditions.checkNotNull(branchId);
		Preconditions.checkNotNull(fullClassName);
		Preconditions.checkNotNull(contents);

		String pageTitle = getJavadocPageName(projectKey, repoSlug, branchId, fullClassName);
		String rootId = getUmlRootId(spaceKey, projectKey, repoSlug, branchId, javadocRootParentId);

		ConfluencePage page = createPage(pageTitle, spaceKey, contents, rootId);
		putLabel(page.getId(), String.format(Consts.LABEL_TEMPLATE, projectKey, repoSlug, branchId));
		return page;
	}

	/**
	 * Puts a label on a page with given id
	 *
	 * @param pageId
	 *            id of the page that is supposed to be given a label
	 * @param labelName
	 *            label name
	 * @return weather the label was put successfully
	 * @throws UnirestException
	 */
	public boolean putLabel(@Nonnull String pageId, @Nonnull String labelName) throws UnirestException {
		Preconditions.checkNotNull(pageId);
		Preconditions.checkNotNull(labelName);

		return Unirest.post(String.format("%s/rest/api/content/%s/label", getBaseUrl(), pageId))
				.header("Content-Type", "application/json").basicAuth(getUsername(), getPassword())
				.body(String.format("[{\"name\": \"%s\"}]", labelName)).asJson().getStatus() == 200;
	}

	/**
	 * Gets a name for a javadoc page for a given project/repo/branch
	 *
	 * @param projectKey
	 *            project's key
	 * @param repoSlug
	 *            repo's slug
	 * @param branchId
	 *            branch's id
	 * @param fullClassName
	 *            full name of the javadoced class (including package)
	 * @return the name of javadoc page
	 */
	@Nonnull
	public String getJavadocPageName(@Nonnull String projectKey, @Nonnull String repoSlug, @Nonnull String branchId,
			@Nonnull String fullClassName) {
		return String.format("%s" + Consts.SUFFIX_TEMPLATE, fullClassName, projectKey, repoSlug, branchId);
	}

	/**
	 * Gets the id a javadoc root page for given project/repo/branch in the
	 * given space. Creates the page if necessary.
	 *
	 * @param spaceKey
	 *            space's key
	 * @param projectKey
	 *            project's key
	 * @param repoSlug
	 *            repo's slug
	 * @param branchId
	 *            branch's id
	 * @param javadocRootParentId
	 *            id of the parent of the javadoc root page, may be null; there
	 *            is no checking that the existing page is a child of this page
	 * @return id of the javadoc root page
	 */
	@Nonnull
	public String getJavadocRootId(@Nonnull String spaceKey, @Nonnull String projectKey, @Nonnull String repoSlug,
			@Nonnull String branchId, @Nullable String javadocRootParentId) throws UnirestException {

		return getRootId(spaceKey, projectKey, repoSlug, branchId, javadocRootParentId, "javadoc");
	}

	/**
	 * Gets the id a uml root page for given project/repo/branch in the given
	 * space. Creates the page if necessary.
	 *
	 * @param spaceKey
	 *            space's key
	 * @param projectKey
	 *            project's key
	 * @param repoSlug
	 *            repo's slug
	 * @param branchId
	 *            branch's id
	 * @param javadocRootParentId
	 *            id of the parent of the javadoc root page, may be null; there
	 *            is no checking that the existing page is a child of this page
	 * @return id of the javadoc root page
	 */
	@Nonnull
	public String getUmlRootId(@Nonnull String spaceKey, @Nonnull String projectKey, @Nonnull String repoSlug,
			@Nonnull String branchId, @Nullable String javadocRootParentId) throws UnirestException {

		return getRootId(spaceKey, projectKey, repoSlug, branchId, javadocRootParentId, "uml");
	}

	@Nonnull
	private String getRootId(@Nonnull String spaceKey, @Nonnull String projectKey, @Nonnull String repoSlug,
			@Nonnull String branchId, @Nullable String javadocRootParentId, String rootPageName)
					throws UnirestException {
		if (javadocRootId != null)
			return javadocRootId;

		Preconditions.checkNotNull(spaceKey);
		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repoSlug);
		Preconditions.checkNotNull(branchId);

		String javadocTitle = getJavadocPageName(projectKey, repoSlug, branchId, rootPageName);
		// === javadoc [NAATLAS/autodoc/master]

		Optional<ConfluencePage> javadocRoot = findPage(spaceKey, javadocTitle);

		return Optionals.orElseGetThrowing(javadocRoot, () -> {
			log.debug("Root page not found. Creating...");
			ConfluencePage page = createPage(javadocTitle, spaceKey, "JAVADOC ROOT", javadocRootParentId);
			putLabel(page.getId(), String.format(Consts.LABEL_TEMPLATE, projectKey, repoSlug, branchId));
			javadocRootId = page.getId();
			return page;
		}).getId();
	}

	/**
	 * Finds a Confluence page with given title, located in given space
	 *
	 * @param spaceKey
	 *            space to search in
	 * @param pageTitle
	 *            page title to search for
	 * @return a representation of a found confluence page on
	 *         {@link Optional#empty()}
	 * @throws UnirestException
	 */
	@Nonnull
	public Optional<ConfluencePage> findPage(String spaceKey, String pageTitle) throws UnirestException {
		Optional<ConfluencePage> pageFound = Unirest.get(getBaseUrl() + "/rest/api/content/search")
				.basicAuth(getUsername(), getPassword())
				.queryString("cql", String.format("space='%s' AND title='%s'", spaceKey, pageTitle))
				.queryString("expand", "version").asObject(ContentSearchPage.class).getBody().getResults().stream()
				.findFirst();
		if (pageFound.isPresent()) {
			log.debug("Found page ({}/{}) with id: {}", spaceKey, pageTitle, pageFound.get().getId());
		} else {
			log.debug("Page ({}/{}) not found", spaceKey, pageTitle);
		}
		return pageFound;
	}

	/**
	 * Removes all javadoc pages related to a given project/repo/branch in a
	 * given space
	 *
	 * @param spaceKey
	 *            key of the space containing the pages to be removed
	 * @param projectKey
	 *            key of the stash project related to the pages to be removed
	 * @param repoSlug
	 *            slug of the repo related to the pages to be removed
	 * @param branchId
	 *            id of the branch related to the pages to be removed
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

		HttpResponse<ContentSearchPage> response = Unirest.get(url).queryString("cql", cql).queryString("limit", 9999)
				.queryString("expand", "space,version").basicAuth(getUsername(), getPassword())
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
	 * @param id
	 *            id of the page to be removed
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
	 * @param page
	 *            page to remove
	 * @return weather the page has been removed
	 * @throws UnirestException
	 */
	public boolean removePage(@Nonnull ConfluencePage page) throws UnirestException {
		Preconditions.checkNotNull(page);

		return removePage(page.getId());
	}

	private List<ConfluencePage> getAll(ContentSearchPage body) throws UnirestException {
		if (body == null || body.getResults() == null || body.getResults().size() == 0)
			return new ArrayList<>();
		List<ConfluencePage> pages = new ArrayList<>(body.getResults());

		String nextUrl;
		while (body.getLinks() != null && (nextUrl = body.getLinks().getNext()) != null) {
			body = Unirest.get(getBaseUrl() + nextUrl).basicAuth(getUsername(), getPassword())
					.asObject(ContentSearchPage.class).getBody();
			pages.addAll(body.getResults());
		}

		return pages;
	}

	/**
	 * Moves the page to a new parent.
	 * 
	 * @param page
	 *            the page to move; has to have title, id, and version set
	 * @param newParentId
	 *            id of the new parent of <code>page</code>
	 * @return a representation of the moved page
	 * @throws UnirestException
	 * @throws com.google.common.base.VerifyException
	 *             when the given page could not be moved
	 */
	@Nonnull
	public ConfluencePage movePage(@Nonnull ConfluencePage page, @Nonnull String newParentId) throws UnirestException {
		Preconditions.checkNotNull(page);
		Preconditions.checkNotNull(newParentId);
		Preconditions.checkNotNull(page.getTitle());
		Preconditions.checkNotNull(page.getId());
		Preconditions.checkNotNull(page.getPageVersion());

		ConfluencePage pageMoveJson = new ConfluencePage();
		pageMoveJson.setId(page.getId());
		pageMoveJson.setTitle(page.getTitle());
		pageMoveJson.setType("page");
		pageMoveJson.setAncestors(ImmutableList.of(new Ancestor(newParentId)));
		pageMoveJson.setPageVersion(new PageVersion(page.getVersionInt() + 1));

		ConfluencePage movedPage = Unirest.put(String.format("%s/rest/api/content/%s", getBaseUrl(), page.getId()))
				.header("Content-Type", "application/json").basicAuth(getUsername(), getPassword()).body(pageMoveJson)
				.asObject(ConfluencePage.class).getBody();

		Verify.verifyNotNull(movedPage.getId(), "Could not move the page");

		return movedPage;
	}

	/**
	 * Searches for page and moves it to the new parent
	 * 
	 * @param spaceKey
	 *            page's space's key
	 * @param title
	 *            page's title
	 * @param newParentId
	 *            is of the new parent
	 * @return a representation of the moved page or {@link Optional#empty()}
	 * @throws UnirestException
	 */
	@Nonnull
	public Optional<ConfluencePage> movePage(@Nonnull String spaceKey, @Nonnull String title,
			@Nonnull String newParentId) throws UnirestException {
		Preconditions.checkNotNull(title);
		Preconditions.checkNotNull(spaceKey);
		Preconditions.checkNotNull(newParentId);

		return Optionals.mapThrowing(findPage(spaceKey, title), page -> movePage(page, newParentId));
	}

}
