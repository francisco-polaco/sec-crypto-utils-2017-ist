package pt.ulisboa.tecnico.meic.sec;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.sql.Timestamp;
import java.util.Scanner;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

/**
 * Created by francisco on 04/03/2017.
 */
public class Application {

    private static final String DELIMITER_STRING = "$DeLiM$";
    private static final String DELIMITER_STRING_REGEX = "\\$DeLiM\\$";


    public static void main(String[] args) throws Exception{
        System.out.println("Write a message:");
        Scanner scanner = new Scanner(System.in);
        String originalMessage = scanner.nextLine();
        CryptoManager cryptoManager = new CryptoManager();

        System.out.println();
        System.out.println("Generating RSA keys ...");
        System.out.println();

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // To get a timestamp
        Timestamp timestamp = cryptoManager.getActualTimestamp();

        System.out.println("Timestamp: \n" + timestamp.toString());
        System.out.println();

        // To get a nonce
        byte[] nonce = cryptoManager.generateNonce(32);
        System.out.println("Nonce: \n" + cryptoManager.convertBinaryToBase64(nonce));
        System.out.println();

        // Assuming the following message
        String message = originalMessage + DELIMITER_STRING +
                timestamp.toString() + DELIMITER_STRING +
                cryptoManager.convertBinaryToBase64(nonce);

        // We can ask for a SHA-2 digest, however is not necessary if we are going to sign afterwards
        byte[] digest = cryptoManager.digest(message.getBytes());
        System.out.println("SHA-2: \n" + cryptoManager.convertBinaryToBase64(digest));
        System.out.println();

        // Let's sign the message using our KeyPair
        byte[] digitalSignature = cryptoManager.makeDigitalSignature(message.getBytes(), keyPair.getPrivate());
        System.out.println("Signature: \n" + cryptoManager.convertBinaryToBase64(digitalSignature));
        System.out.println();

        message += DELIMITER_STRING + cryptoManager.convertBinaryToBase64(digitalSignature);

        // To generate a key
        Key aesKey = cryptoManager.generateAESKey(128);
        byte[] encoded = aesKey.getEncoded();
        System.out.println("AES Key:");
        System.out.println(printHexBinary(encoded));
        System.out.println();
        // To generate an IV
        byte[] iv = cryptoManager.generateIV(16);
        System.out.println("IV: \n" + cryptoManager.convertBinaryToBase64(iv));
        System.out.println();

        // Finally let's encrypt everything.
        final byte[] encryptedMessage = cryptoManager.runAES(message.getBytes(), aesKey, iv, Cipher.ENCRYPT_MODE);
        System.out.println("Encrypted Message:");
        System.out.println(cryptoManager.convertBinaryToBase64(encryptedMessage));
        System.out.println();

        // Now we need to do the same, but backwards

        // Decrypt again, using the same key and IV
        byte[] incomingMessage = cryptoManager.runAES(encryptedMessage, aesKey, iv, Cipher.DECRYPT_MODE);
        System.out.println("Incoming Message...");
        System.out.println();
        String decryptedMessage = new String(incomingMessage);
        System.out.println("Decrypted Message:");
        System.out.println(decryptedMessage);
        System.out.println();

        String[] messagePieces = decryptedMessage.split(DELIMITER_STRING_REGEX);

        /* Let's retrieve the signature, that is on last position of the array
        *  0 - original text
        *  1 - timestamp
        *  2 - nonce
        *  3 - signature
        *  Note that the concatenation is only needed because we have little representative power with strings :D
        *  I know I know, it's awful :'(
        */
        final boolean verify = cryptoManager.verifyDigitalSignature(cryptoManager.convertBase64ToBinary(messagePieces[3]),
                (messagePieces[0] + DELIMITER_STRING + messagePieces[1] + DELIMITER_STRING + messagePieces[2]).getBytes(),
                keyPair.getPublic());

        if(verify) System.out.println("Signature Valid!!!! :')");
        else {
            System.out.println("Signature INvalid!!!! :'(");
            return;
        }
        System.out.println();

        // Finally we only need to check if the message is fresh.
        final boolean valid = cryptoManager.isTimestampAndNonceValid(Timestamp.valueOf(messagePieces[1]),
                cryptoManager.convertBase64ToBinary(messagePieces[2]));

        if(valid) System.out.println("Fresh! :')");
        else {
            System.out.println("Not fresh! :'(");
            return;
        }
        System.out.println();

        // Let's now retrieve the original message
        System.out.println("You receive the message:");
        System.out.println(messagePieces[0]);

    }
}
