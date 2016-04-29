package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networkedassets.autodoc.transformer.manageSettings.require.SettingsPersistor;
import com.networkedassets.autodoc.transformer.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import java.io.*;
import java.util.Set;

public class JsonSettingsPersistor implements SettingsPersistor {

    private static Logger log = LoggerFactory.getLogger(JsonSettingsPersistor.class);

    ObjectsEncryptor objectsEncryptor;
    ObjectMapper objectMapper = new ObjectMapper();

    public JsonSettingsPersistor(ObjectsEncryptor objectsEncryptor) {
        this.objectsEncryptor = objectsEncryptor;
    }

    @Override
    public boolean saveSettingsToFile(String filename, Settings settings) {
        File settingsFile = new File(filename);
        try (FileOutputStream fileOut = new FileOutputStream(settingsFile);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            String settingsJson = objectMapper.writeValueAsString(settings);
            objectOut.writeObject(objectsEncryptor.sealObject(settingsJson));

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

    @Override
    public Settings loadSettingsFromFile(String filename) {
        Settings loadedSettings = new Settings();
        File settingsFile = new File(filename);
        try (FileInputStream fileIn = new FileInputStream(settingsFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileIn)) {
            SealedObject sealedObject = (SealedObject) objectInputStream.readObject();
            String settingsJson = (String) objectsEncryptor.unsealObject(sealedObject);
            loadedSettings = objectMapper.readValue(settingsJson, Settings.class);
            log.debug("Settings loaded from {}", settingsFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            log.warn("Can't load settings from {} - file not found. Creating new default settings...",
                    settingsFile.getAbsolutePath());
        } catch (ClassNotFoundException e) {
            log.warn(
                    "Can't load settings from {} - serialization failed, class not found. Creating new default settings...",
                    settingsFile.getAbsolutePath());
        } catch (IOException e) {
            log.warn("Can't load settings from {}: . Creating new default settings...", settingsFile.getAbsolutePath(), e);
        } catch (BadPaddingException e) {
            log.warn("Bad padding during unsealing settings. Creating new default settings...", e);
        } catch (IllegalBlockSizeException e) {
            log.warn("Illegal block size during unsealing settings. Creating new default settings...", e);
        }
        return loadedSettings;

    }
}
