package com.networkedassets.autodoc.transformer.util;

import com.networkedassets.autodoc.transformer.settings.Settings;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import java.io.*;

/**
 * Created by mtulaza on 2016-02-18.
 */
public class SettingsEncryptorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testEncryptingAndDecryptingWorksProperly() throws IOException, IllegalBlockSizeException, ClassNotFoundException, BadPaddingException {
        File tempFolder = temporaryFolder.newFolder();
        File settingsFile = temporaryFolder.newFile("settingsFile.ser");

        final String CONFLUENCE_URL = "example.url.com";

        Settings settings = new Settings();
        settings.setConfluenceUrl(CONFLUENCE_URL);

        PasswordStoreService passwordService = new PasswordStoreService(tempFolder.getAbsolutePath() + "iamnotpassword.txt");
        SettingsEncryptor settingsEncryptor = new SettingsEncryptor(passwordService.getPassword(), passwordService.getRandomSalt(10));

        SealedObject sealedObject = settingsEncryptor.buildSealedObjectFrom(settings);
        FileOutputStream fos = new FileOutputStream(settingsFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(sealedObject);

        FileInputStream fileIn = new FileInputStream(settingsFile);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileIn);
        SealedObject sealedObjectRead = (SealedObject) objectInputStream.readObject();

        Assert.assertEquals(CONFLUENCE_URL, settingsEncryptor.buildSettingsObjectFrom(sealedObjectRead).getConfluenceUrl());
    }
}
