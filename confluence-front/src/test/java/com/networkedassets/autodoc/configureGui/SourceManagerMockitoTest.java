package com.networkedassets.autodoc.configureGui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.google.common.collect.Lists;
import com.mashape.unirest.http.HttpResponse;
import com.networkedassets.autodoc.TransformerClient;
import com.networkedassets.autodoc.configuration.SourceManager;
import com.networkedassets.autodoc.transformer.settings.SettingsException;
import com.networkedassets.autodoc.transformer.settings.Source;

@RunWith(MockitoJUnitRunner.class)
public class SourceManagerMockitoTest {

	private SourceManager sourceManager;
	private List<Source> appLinksSources = Lists.newArrayList();

	@Mock
	TransformerClient mockTransformerClient;
	@Mock
	ApplicationLinkService mockAppLinksService;
	@Mock
	HttpResponse<String> mockHttpResponse;

	@Before
	public void setUp() {
		appLinksSources.add(createSource(0, "appLinksId0"));
		sourceManager = new SourceManager(mockAppLinksService, mockTransformerClient);
		sourceManager.setAppLinksSources(appLinksSources);
	}

	@Test
	public void testUpdateExistingSourcesFromAppLinksWhenSourcesAreDifferent() throws SettingsException {

		List<Source> currentSources = Lists.newArrayList();
		currentSources.add(createSource(1, "appLinksId1"));

		when(mockHttpResponse.getBody()).thenReturn("0");
		when(mockTransformerClient.setSource(appLinksSources.get(0))).thenReturn(mockHttpResponse);
		when(mockTransformerClient.removeSource("1")).thenReturn(mockHttpResponse);
		assertTrue(sourceManager.updateSourcesFromAppLinks(currentSources).get(0).equals(("0")));
	}

	@Test
	public void testUpdateExistingSourcesFromAppLinksWhenCurrentSourcesIsEmpty() throws SettingsException {

		List<Source> currentSources = Lists.newArrayList();

		when(mockHttpResponse.getBody()).thenReturn("0");
		when(mockTransformerClient.setSource(appLinksSources.get(0))).thenReturn(mockHttpResponse);

		assertTrue(sourceManager.updateSourcesFromAppLinks(currentSources).get(0).equals(("0")));

	}

	@Test
	public void testUpdateExistingSourcesFromAppLinksWhenSourcesArePartiallyCommon() throws SettingsException {

		List<Source> currentSources = Lists.newArrayList();
		currentSources.add(createSource(0, "appLinksId0"));
		currentSources.add(createSource(1, "appLinksId1"));

		when(mockHttpResponse.getBody()).thenReturn("0");
		when(mockTransformerClient.changeSource("0",currentSources.get(0))).thenReturn(mockHttpResponse);
		when(mockTransformerClient.removeSource("1")).thenReturn(mockHttpResponse);
		assertTrue(sourceManager.updateSourcesFromAppLinks(currentSources).get(0).equals(("0")));

	}

	private Source createSource(int id, String appLinksId) {
		Source source = new Source();
		source.setId(id);
		source.setAppLinksId(appLinksId);
		return source;
	}

}
