# Java-Crypto-Utils

Well some families asked for a good API to use Java Crypto in their project.
In record time, we delivered.

## Compilation and Installation
The easy way to do this is by using maven and simply running the below command.
``` 
mvn install
```
Now you can edit your project's POM and add the following dependency:
``` 
<groupId>pt.ulisboa.tecnico.meic.sec</groupId>
<artifactId>crypto</artifactId>
<version>1.0-SNAPSHOT</version>
```
You are now ready to use one of the best API out there :smile:

## Usage
You only need to instantiate an object of the class CryptoManager.
``` 
CryptoManager cryptoManager = new CryptoManager();
``` 
After instantiate, you can call all the methods shown below on the object.<br>
Also, some utility functions are available statically at CryptoUtilities class.

## API Reference
### CryptoManager Class

| Method     | What it does | Parameters | Returns|
| ------------- |---------------------|------|--------|
| generateNounce | Generates a Nounce with the size given | (int bytes) | byte[] |
| getActualTimestamp | Returns the Actual Time Timestamp  | (void) | java.sql.Timestamp |
| isTimestampAndNonceValid | Checks if the Timestamp is fresh and if the pair Timestamp, Nonce was already seen.  | (Timestamp date, byte[] nounce) | boolean |
| convertBinaryToBase64 | Binary -> Base64  | (byte[] data) | String |
| convertBase64ToBinary | Base64 -> Binary  | (String data) | byte[] |
| digest | SHA-2(toBeDigested) | (byte[] toBeDigested) | byte[] |
| makeDigitalSignature | Signature(SHA-2(toBeDigested)) | (byte[] bytesToSign, KeyPair keyPair) | byte[] |
| verifyDigitalSignature | Checks if the signature is valid. | (byte[] signedDigest, byte[] bytesToBeVerified, PublicKey publicKey) | boolean |
| runAES | Encrypts/Decrypts using AES-CBC Algorithm | (byte[] bytesToEncrypt, Key aesKey, byte[] iv) | byte[] |
| generateIV | Generates a Secure Random IV | (int bytes) | byte[] |
| generateAESKey | Generates an AES Key | (int bits) | Key |


### CryptoUtilities Class
Note that all methods below are static.

| Method     | What it does | Parameters | Returns|
| ------------- |---------------------|------|--------|
| getPrivateKeyFromKeystore | Retrieves the PrivateKey from the given KeyStore | (KeyStore keystore, String keyAlias, char[] keyPassword) | PrivateKey|
| getPublicKeyFromKeystore | Retrieves the PublicKey from the given KeyStore | (KeyStore keystore, String keyAlias, char[] keyPassword) | PublicKey|
| readKeystoreFile | Loads into memory a KeyStore that is written to a file | (String keyStoreFilePath, char[] keyStorePassword) | KeyStore|
| readAESKey | Loads into memory an AES key that is written to a file | (String keyPath) | Key |
| writeAESKey | Writes into disk an AES key | (String keyPath, Key symmetricKey) | void |


## Acknowledgments
I thank my pet Nicky that didn't allow me to have a full hour of work on this library.
