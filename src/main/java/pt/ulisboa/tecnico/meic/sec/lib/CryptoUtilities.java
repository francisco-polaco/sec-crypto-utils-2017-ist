package pt.ulisboa.tecnico.meic.sec.lib;

import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.*;

public class CryptoUtilities {
    /**
     * @param keystore
     * @param keyAlias
     * @param keyPassword
     * @return
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public static PrivateKey getPrivateKeyFromKeystore(KeyStore keystore, String keyAlias, char[] keyPassword)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (PrivateKey) keystore.getKey(keyAlias, keyPassword);
    }

    /**
     * @param keystore
     * @param keyAlias
     * @param keyPassword
     * @return
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public static PublicKey getPublicKeyFromKeystore(KeyStore keystore, String keyAlias, char[] keyPassword)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        final java.security.cert.Certificate cert = keystore.getCertificate(keyAlias);
        final PublicKey publicKey = cert.getPublicKey();
        return publicKey;
    }

    /**
     * @param keystore
     * @param keyAlias
     * @param keyPassword
     * @return
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public static Key getAESKeyFromKeystore(KeyStore keystore, String keyAlias, char[] keyPassword)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (Key) keystore.getKey(keyAlias, keyPassword);
    }

    /**
     * Reads a KeyStore from disk.
     * @param keyStoreFilePath
     * @param keyStorePassword
     * @return KeyStore
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public static KeyStore readKeystoreFile(String keyStoreFilePath, char[] keyStorePassword)
            throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        FileInputStream fis = new FileInputStream(keyStoreFilePath);
        KeyStore keystore = KeyStore.getInstance("JCEKS");
        keystore.load(fis, keyStorePassword);
        return keystore;
    }

    /**
     * Reads the keyPath and extracts its Symmetric Key.
     * @param keyPath
     * @return Key
     * @throws IOException
     */
    public static Key readAESKey(String keyPath) throws IOException {
        byte[] encoded;
        try(FileInputStream fis = new FileInputStream(keyPath)) {
            encoded = new byte[fis.available()];
            fis.read(encoded);
        }catch (IOException e){
            // Just to ensure that the Stream is always closed with a try resource
            throw e;
        }
        return new SecretKeySpec(encoded, "AES");
    }

    /**
     * Writes the Symmetric Key in the keyPath.
     * @param keyPath
     * @param aesKey
     * @throws IOException
     */
    public static void writeAESKey(String keyPath, Key aesKey) throws IOException {
        byte[] encoded = aesKey.getEncoded();
        try(FileOutputStream fos = new FileOutputStream(keyPath)) {
            fos.write(encoded);
        }catch (IOException e){
            // Just to ensure that the Stream is always closed with a try resource
            throw e;
        }
    }
}
