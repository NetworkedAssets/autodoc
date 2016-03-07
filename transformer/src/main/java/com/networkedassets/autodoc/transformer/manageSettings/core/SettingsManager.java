package com.networkedassets.autodoc.transformer.manageSettings.core;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.networkedassets.autodoc.transformer.manageSettings.require.SettingsPersistor;
import com.networkedassets.autodoc.transformer.util.*;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.HookActivatorFactory;
import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.ScheduledEventJob;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.BranchModifier;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.EventScheduler;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SettingsSaver;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SourceCreator;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SourceModifier;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.SourceRemover;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SourceProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Credentials;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.Source;

/**
 * Handles the settings of the application
 */
public class SettingsManager implements SettingsProvider, SettingsSaver, SourceProvider, SourceCreator, SourceRemover,
		SourceModifier, BranchModifier, EventScheduler {
	private static Logger log = LoggerFactory.getLogger(SettingsManager.class);

	public Scheduler scheduler;
	private SettingsPersistor settingsPersistor;

	private Settings settings = new Settings();
	public String settingsFilename;

	@Inject
	public SettingsManager(Scheduler scheduler, SettingsPersistor settingsPersistor) {
		this.scheduler = scheduler;
		this.settingsPersistor = settingsPersistor;
		settingsFilename = SettingsUtils.getSettingsFilenameFromProperties();

		setSettings(settingsPersistor.loadSettingsFromFile(settingsFilename));

		getSettings().getTransformerSettings().setAddress(PropertyHandler.getInstance().getValue("jetty.address"),
				Integer.parseInt(PropertyHandler.getInstance().getValue("jetty.port")));
		log.info("Address is setup to: " + getSettings().getTransformerSettings().getAddress());
		updateAllSourcesAndEnableHooks();
	}

	private void updateAllSourcesAndEnableHooks() {
		this.settings.getSources().forEach(source -> {
			try {
				SettingsUtils.updateProjectsFromRemoteSource(source);
				HookActivatorFactory.getInstance(source, this.settings.getTransformerSettings().getAddress())
						.enableAllHooks();
			} catch (MalformedURLException e) {
				log.error("Source {} has malformed URL. Can't load projects: ", source.toString(), e);
			}
		});
	}

	@Override
	public Settings getNotUpdatedSettings() {
		return getSettings();
	}

	@Override
	public Settings getCurrentSettings() {
		updateAllSourcesAndEnableHooks();
		return settings;
	}

	@Override
	public boolean setCredentials(Settings settings) {
		this.settings.setCredentials(new Credentials());
		this.settings.getCredentials().setConfluencePassword(settings.getCredentials().getConfluencePassword());
		this.settings.setConfluenceUrl(settings.getConfluenceUrl());
		this.settings.getCredentials().setConfluenceUsername(settings.getCredentials().getConfluenceUsername());
		return settingsPersistor.saveSettingsToFile(settingsFilename, getSettings());
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
		source.setId(-1);
		if (Strings.isNullOrEmpty(source.getUsername()) || Strings.isNullOrEmpty(source.getPassword())) {
			source.setUsername(getSettings().getCredentials().getConfluenceUsername());
			source.setPassword(getSettings().getCredentials().getConfluencePassword());
		}
		SettingsUtils.verifySource(source, settings.getSources());
		source.setId(getSettings().totalId);
		if (source.isCorrect()) {
			getSettings().totalId++;
			getSettings().getSources().add(source);
		}
		settingsPersistor.saveSettingsToFile(settingsFilename, getSettings());
		return source;
	}

	@Override
	public boolean removeSource(int sourceId) {
		boolean removed = getSettings().getSources().removeIf(source -> source.getId() == sourceId);
		if (removed) {
			settingsPersistor.saveSettingsToFile(settingsFilename, getSettings());
		}
		return removed;
	}

	@Override
	public Source modifySource(Source source) {
		if (Strings.isNullOrEmpty(source.getPassword())) {
			SettingsUtils.setCorrectPasswordForSource(source, settings.getSources());
		}
		SettingsUtils.verifySource(source, settings.getSources());
		if (source.isCorrect()) {
			getSettings().getSources().removeIf(s -> s.getId() == source.getId());
			getSettings().getSources().add(source);
			settingsPersistor.saveSettingsToFile(settingsFilename, getSettings());
		}
		return source;
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
		currentBranch.setScheduledEvents(new ArrayList<>(branch.getScheduledEvents()));
		settingsPersistor.saveSettingsToFile(settingsFilename, getSettings());
		return currentBranch;
	}

	@Override
	public void scheduleEvents(Branch currentBranch, int sourceId, String projectKey, String repoSlug,
			String branchId) {

		Preconditions.checkNotNull(currentBranch);
		Preconditions.checkNotNull(scheduler);
		try {
			scheduler.clear();

			currentBranch.getScheduledEvents().stream().forEach(event -> {
				try {
					JobDetail job = newJob(ScheduledEventJob.class)
							.usingJobData("sourceUrl", getCurrentSettings().getSourceById(sourceId).getUrl())
							.usingJobData("projectKey", projectKey).usingJobData("repoSlug", repoSlug)
							.usingJobData("branchId", branchId)
							.usingJobData("latestCommit", currentBranch.getLatestCommit()).build();

					Trigger trigger = newTrigger().startNow().withSchedule(ScheduledEventHelper.getCronSchedule(event))
							.build();

					log.debug("Scheduled event {} with cron: {}", event.toString(),
							((CronTrigger) trigger).getCronExpression());
					log.debug("Scheduled event has latestCommit hash: {}", currentBranch.getLatestCommit());

					scheduler.scheduleJob(job, trigger);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			});

			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
		settingsPersistor.saveSettingsToFile(settingsFilename, getSettings());
	}
}
