package com.networkedassets.autodoc.configureGui;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.application.stash.StashApplicationType;
import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.networkedassets.autodoc.TransformerClient;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.settings.Source.SourceType;
import com.networkedassets.util.functional.Throwing;

public class SourceManager {

	private static final Logger log = LoggerFactory.getLogger(SourceManager.class);

	private final TransformerClient transformerClient;
	private final ApplicationLinkService appLinkService;

	public SourceManager(ApplicationLinkService appLinkService, TransformerClient transformerClient) {

		this.appLinkService = appLinkService;
		this.transformerClient = transformerClient;

	}

	//TODO:Refactor
	public List<String> updateSourceFromAppLinks(List<Source> currentSources) {

		Map<String, Source> appLinksSources = Maps.uniqueIndex(getSourcesFromAppLinks(), Source::getAppLinksId);

		List<String> changedSources = currentSources.stream()
				.filter(s -> appLinksSources.containsKey(s.getAppLinksId()))
				.map(Throwing.rethrowAsRuntimeException(this::updateSource)).collect(Collectors.toList());
		List<String> addedSources = currentSources.stream()
				.filter(s -> !Strings.isNullOrEmpty(s.getAppLinksId())
						&& !appLinksSources.containsKey(s.getAppLinksId()))
				.map(Throwing.rethrowAsRuntimeException(this::addSource)).collect(Collectors.toList());
		currentSources.stream()
				.filter(s -> !Strings.isNullOrEmpty(s.getAppLinksId())
						&& !appLinksSources.containsKey(s.getAppLinksId()))
				.map(Throwing.rethrowAsRuntimeException(this::removeSource)).collect(Collectors.toList());

		return Stream.concat(addedSources.stream(), changedSources.stream()).collect(Collectors.toList());

	}

	private List<Source> getSourcesFromAppLinks() {

		List<Source> sources = Lists.newArrayList();
		appLinkService.getApplicationLinks(StashApplicationType.class).forEach(appLinks -> {

			Optional<SourceType> sourceType = getAppLinkSourceType(appLinks);
			if (sourceType.isPresent()) {
				sources.add(createSourceEntity(appLinks, sourceType.get()));
			}
		});
		return sources;
	}

	private Source createSourceEntity(ApplicationLink appLinks, SourceType sourceType) {
		Source source = new Source();
		source.setName(appLinks.getName());
		source.setUrl(appLinks.getRpcUrl().toString());
		source.setSourceType(sourceType);
		source.setAppLinksId(appLinks.getId().toString());
		return source;
	}

	private String addSource(Source source) throws SettingsException {

		return transformerClient.setSource(source).getBody();
	}

	private String updateSource(Source source) throws SettingsException {

		return transformerClient.changeSource(String.valueOf(source.getId()), source).getBody();

	}

	private String removeSource(Source source) throws SettingsException {

		return transformerClient.removeSource(String.valueOf(source.getId())).getBody();

	}

	private Optional<SourceType> getAppLinkSourceType(ApplicationLink appLink) {
		String requestUrl = "/rest/api/1.0/application-properties";
		Optional<SourceType> sourceType = Optional.empty();

		ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory();

		try {
			ApplicationLinkRequest request = requestFactory.createRequest(MethodType.GET, requestUrl);
			sourceType = request.executeAndReturn(
					new ReturningResponseHandler<com.atlassian.sal.api.net.Response, Optional<SourceType>>() {
						@Override
						public Optional<SourceType> handle(com.atlassian.sal.api.net.Response response)
								throws ResponseException {
							if (response.isSuccessful() || response.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
								return Optional.of(response.getResponseBodyAsString().contains("Stash")
										? SourceType.STASH : SourceType.BITBUCKET);
							}
							throw new ResponseException(
									String.format("Execute applink with error! [statusCode=%s, statusText=%s]",
											response.getStatusCode(), response.getStatusText()));
						}
					});

		} catch (CredentialsRequiredException | ResponseException e) {
			log.error("Couldn't get appLinks", e);
		}

		return sourceType;

	}

}
