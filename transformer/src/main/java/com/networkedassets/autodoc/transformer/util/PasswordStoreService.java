package com.networkedassets.autodoc.transformer.util;

import com.google.common.io.Files;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/*
    please propose better name if possible..

    Class responsible for building and storing random password in
    the file next to the transformer.jar file
 */
public class PasswordStoreService {
    private static Logger log = LoggerFactory.getLogger(PasswordStoreService.class);

    private File file;

    public PasswordStoreService(String filename) {
        this.file = new File(filename);
    }

    private void savePassword(String password) {
        try {
            Files.write(password, file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Cannot create the file containing password" + e);
        }
    }

    private Optional<String> readPassword() {
        try {
            return Optional.of(Files.readFirstLine(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Cannot find the file containing password" + e);
            return Optional.empty();
        }
    }

    private boolean fileExists() {
        return file.exists() && !file.isDirectory();
    }

    public String getPassword() {
        Optional<String> savedPassword = readPassword();
        if(fileExists() && savedPassword.isPresent()) {
            log.info("File exists, password will be taken from that file..");
            return savedPassword.get();
        }
        log.info("File doesn't exist, password will be generated..");
        String password = RandomStringUtils.randomAlphanumeric(30);
        savePassword(password);
        return password;
    }

    public String getRandomSalt(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
