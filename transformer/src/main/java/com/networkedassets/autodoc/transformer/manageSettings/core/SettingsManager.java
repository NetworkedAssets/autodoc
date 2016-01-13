package com.networkedassets.autodoc.transformer.manageSettings.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.networkedassets.autodoc.clients.atlassian.api.StashBitbucketClient;
import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.ClientConfigurator;
import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.HookActivatorFactory;
import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.ProjectsProviderFactory;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.*;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SourceProvider;
import com.networkedassets.autodoc.transformer.manageSettings.require.HookActivator;
import com.networkedassets.autodoc.transformer.manageSettings.require.ProjectsProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Project;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.util.PropertyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Handles the settings of the application
 */
public class SettingsManager implements SettingsProvider, SettingsSaver, SourceProvider, SourceCreator, SourceRemover,
		SourceModifier, BranchModifier {

	public String settingsFilename;

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
		saveSettingsToFile(settingsFilename);
	}

	private Settings settings = new Settings();
	private static Logger log = LoggerFactory.getLogger(SettingsManager.class);

	public SettingsManager() {
		settingsFilename = getSettingsFilenameFromProperties();
		loadSettingsFromFile(settingsFilename);
		updateSettings(this.settings);
	}

	@Override
	public Settings getCurrentSettings() {
		updateSettings(this.settings);
		return settings;
	}

	private void updateSettings(Settings givenSettings) {
		// drop sources that aren't in the current settings
		// (to add source - use special endpoint)
		givenSettings.getSources().removeIf(s -> settings.getSourceByUrl(s.getUrl()) == null);

		givenSettings.getSources().forEach(source -> {
			try {
				updateProjectsFromRemoteSource(source);
				HookActivator hookActivator = HookActivatorFactory.getInstance(source,
						givenSettings.getTransformerSettings().getLocalhostAddress());
				hookActivator.enableAllHooks();
			} catch (MalformedURLException e) {
				log.error("Source {} has malformed URL. Can't load projects: ", source.toString(), e);
			}
		});

		// add sources from current settings that don't appear in givenOnes
		List<Source> sourcesToAdd = new ArrayList<>();
		settings.getSources().stream().forEach(source -> {
			if (givenSettings.getSourceByUrl(source.getUrl()) == null) {
				sourcesToAdd.add(source);
			}
		});
		givenSettings.getSources().addAll(sourcesToAdd);
	}

	private boolean saveSettingsToFile(String filename) {
		File settingsFile = new File(filename);
		try (FileOutputStream fileOut = new FileOutputStream(settingsFile);
				ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
			objectOut.writeObject(settings);
			log.debug("Settings saved to {}", settingsFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			log.error("Can't save settings to {} - file was not found: ", settingsFile.getAbsolutePath(), e);
			return false;
		} catch (IOException e) {
			log.error("Can't save settings to {} - general IO problem: ", settingsFile.getAbsolutePath(), e);
			return false;

		}

		return true;
	}

	private void loadSettingsFromFile(String filename) {
		File settingsFile = new File(filename);
		try (FileInputStream fileIn = new FileInputStream(settingsFile);
				ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
			settings = (Settings) objectIn.readObject();
			log.debug("Settings loaded from {}", settingsFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			log.error("Can't load settings from {} - file not found. Creating new default settings...",
					settingsFile.getAbsolutePath());
		} catch (ClassNotFoundException e) {
			log.error(
					"Can't load settings from {} - serialization failed, class not found. Creating new default settings...",
					settingsFile.getAbsolutePath());
		} catch (IOException e) {
			log.error("Can't load settings from {}: ", settingsFile.getAbsolutePath(), e);
		}
	}

	private String getSettingsFilenameFromProperties() {

		final String defaultFilename = "transformerSettings.ser";
		PropertyHandler propertyHandler;
		try {
			propertyHandler = PropertyHandler.getInstance();
		} catch (IOException e) {
			log.error("Couldn't load the properties file", e);
			return defaultFilename;
		}
		return propertyHandler.getValue("settings.filename", defaultFilename);
	}

	@SuppressWarnings("CodeBlock2Expr")
	private void updateProjectsFromRemoteSource(@Nonnull Source source) throws MalformedURLException {
		// Get projects from source
		ProjectsProvider projectsProvider = ProjectsProviderFactory.getInstance(source);

		Map<String, Project> sourceProjects = projectsProvider.getProjects();

		// Delete projects, repos and branches in settings, that don't exist in
		// stash (anymore)
		source.projects.values().removeIf(project -> !sourceProjects.containsKey(project.key));
		source.projects.values().forEach(project -> project.repos.values()
				.removeIf(repo -> !sourceProjects.get(project.key).repos.containsKey(repo.slug)));
		source.projects.values()
				.forEach(project -> project.repos.values()
						.forEach(repo -> repo.branches.values()
								.removeIf(branch -> !sourceProjects.get(project.key).repos.get(repo.slug).branches
										.containsKey(branch.id))));

		// Add new branches, repos, and projects from stash
		sourceProjects.values().forEach(stashProject -> {
			source.projects.putIfAbsent(stashProject.key, stashProject);
		});
		sourceProjects.values().forEach(stashProject -> {
			stashProject.repos.values().forEach(stashRepo -> {
				source.getProjectByKey(stashProject.key).repos.putIfAbsent(stashRepo.slug, stashRepo);
			});
		});
		sourceProjects.values().forEach(stashProject -> {
			stashProject.repos.values().forEach(stashRepo -> {
				stashRepo.branches.values().forEach(stashBranch -> {
					source.getProjectByKey(stashProject.key).getRepoBySlug(stashRepo.slug).branches
							.putIfAbsent(stashBranch.id, stashBranch);
				});
			});
		});

		// Update changed non-indexing data
		sourceProjects.values().forEach(stashProject -> {
			Project project = source.getProjectByKey(stashProject.key);
			if (!project.name.equals(stashProject.name)) {
				project.name = stashProject.name;
			}
		});
		sourceProjects.values().forEach(stashProject -> {
			stashProject.repos.values().forEach(stashRepo -> {
				stashRepo.branches.values().forEach(stashBranch -> {
					Branch branch = source.getProjectByKey(stashProject.key).getRepoBySlug(stashRepo.slug)
							.getBranchById(stashBranch.id);
					if (!branch.displayId.equals(stashBranch.displayId)) {
						branch.displayId = stashBranch.displayId;
					}
				});
			});
		});
	}

	@Override
	public boolean setConfluenceCredentials(Settings settings) {

		this.settings.setConfluencePassword(settings.getConfluencePassword());
		this.settings.setConfluenceUrl(settings.getConfluenceUrl());
		this.settings.setConfluenceUsername(settings.getConfluenceUsername());
		return saveSettingsToFile(settingsFilename);
	}

	@Override
	public Optional<Source> getSourceById(int id) {
		return getCurrentSettings().getSources().stream().filter(source -> source.getId() == id).findFirst();
	}

	@Override
	public List<Source> getAllSources() {
		return getCurrentSettings().getSources();
	}

	@Override
	public Source addSource(Source source) {
		verifySource(source);
		if (source.isCorrect()) {
			source.setId(Source.totalId);
			Source.totalId++;
			getSettings().getSources().add(source);
		}
		saveSettingsToFile(settingsFilename);
		return source;
	}

	@Override
	public boolean removeSource(int sourceId) {
		boolean removed = getSettings().getSources().removeIf(source -> source.getId() == sourceId);
		if (removed) {
			saveSettingsToFile(settingsFilename);
		}
		return removed;
	}

	@Override
	public Source modifySource(Source source) {
		if (Strings.isNullOrEmpty(source.getPassword())) {
			setCorrectPasswordForSource(source);
		}
		verifySource(source);
		if (source.isCorrect()) {
			getSettings().getSources().removeIf(s -> s.getId() == source.getId());
			getSettings().getSources().add(source);
			saveSettingsToFile(settingsFilename);
		}
		return source;
	}

	private void setCorrectPasswordForSource(Source source) {
		String previousPassword = this.settings.getSources().stream()
				.filter(previousSources -> previousSources.getId() == source.getId()).map(Source::getPassword)
				.findFirst().orElse(null);
		source.setPassword(previousPassword);

	}

	/**
	 * Sets verification parameters of the source
	 *
	 * @param source
	 *            Source that will be checked for all conditions and proper
	 *            flags will be set on it
	 */
	private void verifySource(Source source) {
		source.setSourceExists(false);
		source.setCredentialsCorrect(false);
		source.setNameCorrect(false);
		source.setSourceTypeCorrect(false);

		// is name unique and not empty
		if (!Strings.isNullOrEmpty(source.getName()) && !settings.getSources().stream()
				.anyMatch(s -> s.getName().equals(source.getName()) && s.getId() != source.getId())) {
			source.setNameCorrect(true);
		}

		try {
			// check for connection data correctness
			StashBitbucketClient stashBitbucketClient = ClientConfigurator.getConfiguredStashBitbucketClient(source);
			if (stashBitbucketClient.isVerified()) {
				source.setSourceExists(true);
				source.setCredentialsCorrect(true);
			} else if (stashBitbucketClient.doesExist()) {
				source.setSourceExists(true);
			}

			/*
			 * check if sourceType matches what lays at the end of given url
			 * link has to be verified user to get appProperties on bitbucket
			 * (but not on stash)
			 */
			if (source.isCredentialsCorrect() && Objects.nonNull(source.getSourceType())) {
				Boolean isSourceTypeRight = stashBitbucketClient.getApplicationProperties()
						.map(ap -> ap.getDisplayName().equalsIgnoreCase(source.getSourceType().toString()))
						.orElse(false);
				source.setSourceTypeCorrect(isSourceTypeRight);
			}
		} catch (MalformedURLException ignored) {
		}
	}

	@Override
	public Branch modifyBranch(int sourceId, String projectKey, String repoSlug, String branchId, Branch branch) {
		Preconditions.checkNotNull(projectKey);
		Preconditions.checkNotNull(repoSlug);
		Preconditions.checkNotNull(branchId);
		Preconditions.checkNotNull(branch);

		Branch currentBranch;

		try {
			currentBranch = getCurrentSettings().getSourceById(sourceId).getProjectByKey(projectKey)
					.getRepoBySlug(repoSlug).getBranchById(branchId);
			Verify.verifyNotNull(currentBranch);
		} catch (NullPointerException | VerifyException e) {
			// wrong data given
			return null;
		}

		currentBranch.setListenTo(branch.getListenTo());
		currentBranch.scheduledEvents = new ArrayList<>(branch.scheduledEvents);
		saveSettingsToFile(settingsFilename);
		return currentBranch;
	}
}
