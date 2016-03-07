package com.networkedassets.autodoc.transformer.manageSettings.infrastructure;

import com.networkedassets.autodoc.transformer.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/*
    Class responsible for encrypting and decrypting
    settings object to file
 */
public class ObjectsEncryptor {
    private static Logger log = LoggerFactory.getLogger(ObjectsEncryptor.class);
    private static final String TRANSFORMATION = "AES";

    private Cipher cipher;
    private Cipher decipher;

    private char[] passwordBytes;
    private byte[] saltBytes;

    public ObjectsEncryptor(String password, String salt) {
        this.passwordBytes = password.toCharArray();
        this.saltBytes = salt.getBytes();

        initCipherAndDecipher();
    }

    public void initCipherAndDecipher() {
        try {
            KeySpec spec = new PBEKeySpec(passwordBytes, saltBytes, 1024, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), TRANSFORMATION);

            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            decipher = Cipher.getInstance(TRANSFORMATION);
            decipher.init(Cipher.DECRYPT_MODE, secret);
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm as given to SecretKeyFactory", e);
        } catch (NoSuchPaddingException e) {
            log.error("No such padding as given to Cipher.getInstance()", e);
        } catch (InvalidKeyException e) {
            log.error("Invalid key", e);
        } catch (InvalidKeySpecException e) {
            log.error("Invalid key spec", e);
        }
    }

    public SealedObject sealObject(Serializable object) throws IOException, IllegalBlockSizeException {
        return new SealedObject(object, cipher);
    }

    public Object unsealObject(SealedObject sealedObject) throws ClassNotFoundException,
            BadPaddingException, IllegalBlockSizeException, IOException {
        return sealedObject.getObject(decipher);
    }

}
