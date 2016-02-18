package com.networkedassets.autodoc.transformer.manageSettings.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.HookActivatorFactory;
import com.networkedassets.autodoc.transformer.manageSettings.infrastructure.ScheduledEventJob;
import com.networkedassets.autodoc.transformer.manageSettings.provide.in.*;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SettingsProvider;
import com.networkedassets.autodoc.transformer.manageSettings.provide.out.SourceProvider;
import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.Credentials;
import com.networkedassets.autodoc.transformer.settings.Settings;
import com.networkedassets.autodoc.transformer.settings.Source;
import com.networkedassets.autodoc.transformer.util.ScheduledEventHelper;
import com.networkedassets.autodoc.transformer.util.SettingsUtils;
import net.sourceforge.plantuml.creole.Sea;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.io.*;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Handles the settings of the application
 */
public class SettingsManager implements SettingsProvider, SettingsSaver, SourceProvider, SourceCreator, SourceRemover,
        SourceModifier, BranchModifier, EventScheduler {

    private Settings settings = new Settings();

    //TODO Change place to store password
    private final char[] password =
            "Nobody Expects the Spanish Inquisition".toCharArray();
    private final byte[] salt = "a9v5n38s".getBytes();
    private SecretKey secret;
    private Cipher cipher, decipher;

    private static Logger log = LoggerFactory.getLogger(SettingsManager.class);

    public String settingsFilename;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
        saveSettingsToFile(settingsFilename);
    }

    public Scheduler scheduler;

    @Inject
    public SettingsManager(Scheduler scheduler) {
        this.scheduler = scheduler;
        settingsFilename = SettingsUtils.getSettingsFilenameFromProperties();
        // Create key
        KeySpec spec = new PBEKeySpec(password, salt, 1024, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey tmp = factory.generateSecret(spec);
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            decipher = Cipher.getInstance("AES");
            decipher.init(Cipher.DECRYPT_MODE, secret);
        } catch (InvalidKeySpecException e) {
            log.error("Invalid key spec", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm as given to SecretKeyFactory", e);
        } catch (NoSuchPaddingException e) {
            log.error("No such padding as given to Ciper.getInstance()", e);
        } catch (InvalidKeyException e) {
            log.error("Invalid key", e);
        }

        loadSettingsFromFile(settingsFilename);
        updateAllSourcesAndEnableHooks();
    }

    @Override
    public Settings getCurrentSettings() {
        updateAllSourcesAndEnableHooks();
        return settings;
    }

    @Override
    public Settings getNotUpdatedSettings() {
        return getSettings();
    }

    private void updateAllSourcesAndEnableHooks() {
        this.settings.getSources().forEach(source -> {
            try {
                SettingsUtils.updateProjectsFromRemoteSource(source);
                HookActivatorFactory.getInstance(source, this.settings.getTransformerSettings().getLocalhostAddress())
                        .enableAllHooks();
            } catch (MalformedURLException e) {
                log.error("Source {} has malformed URL. Can't load projects: ", source.toString(), e);
            }
        });
    }

    private boolean saveSettingsToFile(String filename) {
        File settingsFile = new File(filename);
        try (FileOutputStream fileOut = new FileOutputStream(settingsFile);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            SealedObject sealedObject = new SealedObject(settings, cipher);

            objectOut.writeObject(sealedObject);
            log.debug("Settings saved to {}", settingsFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            log.error("Can't save settings to {} - file was not found: ", settingsFile.getAbsolutePath(), e);
            return false;
        } catch (IOException e) {
            log.error("Can't save settings to {} - general IO problem: ", settingsFile.getAbsolutePath(), e);
            return false;
        } catch (IllegalBlockSizeException e) {
            log.error("Illegal block size during sealing settings", e);
            return false;
        }

        return true;
    }

    private void loadSettingsFromFile(String filename) {
        File settingsFile = new File(filename);
        try (FileInputStream fileIn = new FileInputStream(settingsFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileIn)) {
            SealedObject sealedObject = (SealedObject) objectInputStream.readObject();

            settings = (Settings) sealedObject.getObject(decipher);
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
        } catch (BadPaddingException e) {
            log.error("Bad padding during unsealing settings", e);
        } catch (IllegalBlockSizeException e) {
            log.error("Illegal block size during unsealing settings", e);
        }
    }

    @Override
    public boolean setCredentials(Settings settings) {
        this.settings.setCredentials(new Credentials());
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
        if (Strings.isNullOrEmpty(source.getUsername()) || Strings.isNullOrEmpty(source.getPassword())) {
            source.setUsername(getSettings().getConfluenceUsername());
            source.setPassword(getSettings().getConfluencePassword());
        }
        SettingsUtils.verifySource(source, settings.getSources());
        source.setId(Source.totalId);
        if (source.isCorrect()) {
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
            SettingsUtils.setCorrectPasswordForSource(source, settings.getSources());
        }
        SettingsUtils.verifySource(source, settings.getSources());
        if (source.isCorrect()) {
            getSettings().getSources().removeIf(s -> s.getId() == source.getId());
            getSettings().getSources().add(source);
            saveSettingsToFile(settingsFilename);
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
        saveSettingsToFile(settingsFilename);
        return currentBranch;
    }

    @Override
    public void scheduleEvents(Branch currentBranch, int sourceId,
                               String projectKey, String repoSlug, String branchId) {

        Preconditions.checkNotNull(currentBranch);
        Preconditions.checkNotNull(scheduler);
        try {
            scheduler.clear();

            currentBranch.getScheduledEvents().stream().forEach(event -> {
                try {
                    JobDetail job = newJob(ScheduledEventJob.class)
                            .usingJobData("sourceUrl", getCurrentSettings().getSourceById(sourceId).getUrl())
                            .usingJobData("projectKey", projectKey)
                            .usingJobData("repoSlug", repoSlug)
                            .usingJobData("branchId", branchId)
                            .build();

                    Trigger trigger = newTrigger()
                            .startNow()
                            .withSchedule(ScheduledEventHelper.getCronSchedule(event))
                            .build();

                    log.debug("Scheduled event {} with cron: {}", event.toString(), ((CronTrigger) trigger).getCronExpression());
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
}
