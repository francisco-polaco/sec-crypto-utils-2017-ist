package pt.ulisboa.tecnico.meic.sec;

import pt.ulisboa.tecnico.meic.sec.exception.InvalidAESKeySizeException;
import pt.ulisboa.tecnico.meic.sec.exception.NotEnoughNumberOfBytesException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

/**
 * Created by francisco on 04/03/2017.
 */
public class CryptoManager {

    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String DIGEST_ALGORITHM = "SHA-256";
    private static final String SIGNATURE_WITH_DIGEST_ALGORITHM = "SHA256WithRSA";
    private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    private static final long TIME_INTERVAL_VALID_REQUEST_MS = 60 * 1000; // 1 MINUTE

    private ArrayList<String> oldTimestamps;

    public CryptoManager() {
        this.oldTimestamps = new ArrayList<String>();
    }

    /**
     * It returns a byte array with the size given filled with a secure
     * based on SHA1 random stuff.
     * Note that if you want a array with less then 16 bytes, you probably don't
     * know what you are doing.
     *
     * @param byteNumber - number of bytes of the returning byte[]
     * @return randomBytes - byte[] with random suff
     * @throws NoSuchAlgorithmException
     */
    private byte[] getSecureRandomNumber(int byteNumber) throws NoSuchAlgorithmException {
        if(byteNumber < 16) throw new NotEnoughNumberOfBytesException(); // 16 bytes = 128 bits
        byte[] randomBytes = new byte[byteNumber];
        SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    /**
     * It gets a nonce, not much to explain.
     * @param bytes
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] generateNonce(int bytes) throws NoSuchAlgorithmException {
        return getSecureRandomNumber(bytes);
    }

    /**
     * It returns the actual clock and date (obvious) as a Timestamp.
     * @return actualTimestamp
     */
    public Timestamp getActualTimestamp(){
        GregorianCalendar rightNow = new GregorianCalendar();
        return new Timestamp(rightNow.getTimeInMillis());
    }

    /**
     * It will check if the Timestamp is fresh (if it was created within TIME_INTERVAL_VALID_REQUEST_MS
     * from the actual time) and if the pair Timestamp,Nonce was already seen.
     * @param date
     * @param nonce
     * @return boolean true if its valid, false if it isn't
     */
    public boolean isTimestampAndNonceValid(Timestamp date, byte[] nonce) {
        String nonceStr = new String(nonce);
        Timestamp actualTime = getActualTimestamp();

        long actualTimeMs = actualTime.getTime();
        long msgTimeMs = date.getTime();

        if(Math.abs(actualTimeMs - msgTimeMs) < TIME_INTERVAL_VALID_REQUEST_MS) {
            if (oldTimestamps.size() != 0) {
                if (oldTimestamps.contains(date.toString() + nonceStr)) {
                    return false;
                }
            }
            oldTimestamps.add(date.toString() + nonceStr);
        }else {
            return false;
        }
        return true;
    }

    /**
     * It returns the base 64 representation of the given binary.
     * @param binary - binary to be converted
     * @return String
     */
    public String convertBinaryToBase64(byte[] binary){
        return printBase64Binary(binary);
    }

    /**
     * It returns the binary representation of the given base 64.
     * @param text - base64 to be converted
     * @return byte[]
     */
    public byte[] convertBase64ToBinary(String text){
        return parseBase64Binary(text);
    }

    /**
     *  It returns SHA-2 digest of the given byte[]
     *  Don't use this method if you are going to use makeDigitalSignature next.
     *  Don't forget SHA-2 is still secure :D
     * @param toBeDigested - bytes to be digested
     * @return byte[] SHA-2 of toBeDigested
     * @throws NoSuchAlgorithmException
     */
    public byte[] digest(byte[] toBeDigested) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        messageDigest.update(toBeDigested);
        return messageDigest.digest();
    }

    /**
     * It digests the message and signs that digest with senders private key.
     * This method does everything, so you don't need to use digest method before.
     * @param bytesToSign
     * @param keyPair
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public byte[] makeDigitalSignature(byte[] bytesToSign, KeyPair keyPair)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // Digest with SHA256 and sign that digest
        Signature sig = Signature.getInstance(SIGNATURE_WITH_DIGEST_ALGORITHM);
        sig.initSign(keyPair.getPrivate());
        sig.update(bytesToSign);
        return sig.sign();
    }

    /**
     * Checks if the signedDigest was indeed the digest from the message sign with senders private key.
     * Ofc we are checking with a public key :D We are not hackers :D
     * @param signedDigest
     * @param bytesToBeVerified
     * @param publicKey
     * @return boolean - true if it's authentic, false if it isn't
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public boolean verifyDigitalSignature(byte[] signedDigest, byte[] bytesToBeVerified, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // verify the signature with the public key
        Signature sig = Signature.getInstance(SIGNATURE_WITH_DIGEST_ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(bytesToBeVerified);
        try {
            return sig.verify(signedDigest);
        } catch (SignatureException se) {
            System.err.println("Caught exception while verifying " + se);
            return false;
        }
    }

    /**
     * It encrypts/decrypts using AES-CBC bytesToEncrypt using aesKey.
     * It will return the ciphered or the plain text
     * If aesKey has not the proper Padding (PKCS5Padding) an exception will be thrown.
     * @param bytesToEncrypt
     * @param aesKey
     * @param iv
     * @return byte[]
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] runAES(byte[] bytesToEncrypt, Key aesKey, byte[] iv)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // get a AES cipher object and print the provider
        Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);

        IvParameterSpec ips = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ips);
        return cipher.doFinal(bytesToEncrypt);
    }

    /**
     * Returns an IV using secure random
     * @param bytes
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] generateIV(int bytes) throws NoSuchAlgorithmException {
        return getSecureRandomNumber(bytes);
    }

    /**
     * It generates a AES key with the given number of bits
     * @param bits
     * @return
     * @throws NoSuchAlgorithmException
     */
    public Key generateAESKey(int bits) throws NoSuchAlgorithmException {
        if(bits == 128 || bits == 192 || bits == 256){
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(bits);
            return keyGen.generateKey();
        } else {
            throw new InvalidAESKeySizeException(bits);
        }
    }

}
