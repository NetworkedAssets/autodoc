package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.manageSettings.require.SettingsPersistor;
import com.networkedassets.autodoc.transformer.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import java.io.*;

public class SerializingSettingsPersistor implements SettingsPersistor {

    private static Logger log = LoggerFactory.getLogger(SerializingSettingsPersistor.class);

    ObjectsEncryptor objectsEncryptor;

    public SerializingSettingsPersistor(ObjectsEncryptor objectsEncryptor) {
        this.objectsEncryptor = objectsEncryptor;
    }

    @Override
    public boolean saveSettingsToFile(String filename, Settings settings) {
        File settingsFile = new File(filename);
        try (FileOutputStream fileOut = new FileOutputStream(settingsFile);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            objectOut.writeObject(objectsEncryptor.sealObject(settings));

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
            loadedSettings = (Settings) objectsEncryptor.unsealObject(sealedObject);
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
